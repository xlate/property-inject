/*******************************************************************************
 * Copyright (C) 2018 xlate.io LLC, http://www.xlate.io
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package io.xlate.inject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.InjectionPoint;

class PropertyFactory {

    private static final String CLASSPATH = "classpath";
    private static final String USER_DIR = "userdir";
    final Map<String, Properties> propertiesCache;

    PropertyFactory() {
        propertiesCache = new HashMap<>();
    }

    URLStreamHandler classPathHandler(Class<?> beanType) {
        return new ClasspathURLStreamHandler(beanType.getClassLoader());
    }

    URL getResourceUrl(PropertyResource annotation, Class<?> beanType) throws MalformedURLException {
        final String location = annotation.value();
        URL resourceUrl;

        if (location.isEmpty()) {
            StringBuilder resourceName = new StringBuilder(CLASSPATH);
            resourceName.append(':');
            resourceName.append(beanType.getName().replace('.', '/'));
            resourceName.append(".properties");
            resourceUrl = new URL(null, resourceName.toString(), classPathHandler(beanType));
        } else {
            final String resolvedLocation;

            if (annotation.resolveEnvironment()) {
                resolvedLocation = replaceEnvironmentReferences(location);
            } else {
                resolvedLocation = location;
            }

            try {
                final URI resourceId = URI.create(resolvedLocation);
                final String scheme = resourceId.getScheme();

                if (scheme != null) {
                    if (CLASSPATH.equals(scheme)) {
                        resourceUrl = new URL(null, resolvedLocation, classPathHandler(beanType));
                    } if (USER_DIR.equals(scheme)) {
                    	
                    	resourceUrl = new URL("file://" + System.getProperty("user.dir") + "/" + resourceId.getAuthority());
                    	
                    } else {
                    	resourceUrl = resourceId.toURL();
                    }
                } else {
                    resourceUrl = new URL(null, CLASSPATH + ':' + location, classPathHandler(beanType));
                }
            } catch (IllegalArgumentException | MalformedURLException e) {
                throw new InjectionException(e);
            }
        }

        return resourceUrl;
    }

    String getPropertyName(final InjectionPoint point, final String propertyName) {
        if (!propertyName.isEmpty()) {
            return propertyName;
        }

        Member member = point.getMember();
        final String name;

        if (member instanceof Executable) {
            Annotated annotated = point.getAnnotated();
            int p = ((AnnotatedParameter<?>) annotated).getPosition();
            name = member.getName() + ".arg" + p;
        } else {
            name = member.getName();
        }

        return name;
    }

    String getSystemProperty(final Class<?> beanType,
                             final String systemProperty,
                             final String propertyName) {

        final String lookupProperty;

        if (systemProperty.isEmpty()) {
            StringBuilder systemPropertyKey = new StringBuilder();
            systemPropertyKey.append(beanType.getName());
            systemPropertyKey.append('.');
            systemPropertyKey.append(propertyName);
            lookupProperty = systemPropertyKey.toString();
        } else {
            lookupProperty = systemProperty;
        }

        return getProperty(System.getProperties(), lookupProperty, Property.DEFAULT_NULL);
    }

    String getProperty(final URL resourceUrl,
                       final PropertyResourceFormat format,
                       final String propertyName,
                       final String defaultValue) throws IOException {

        final Properties properties = getProperties(resourceUrl, format);
        return getProperty(properties, propertyName, defaultValue);
    }

    Properties getProperties(final URL resourceUrl,
                             final PropertyResourceFormat format) throws IOException {

        final Properties properties;
        final String resourceId = resourceUrl.toString();

        if (propertiesCache.containsKey(resourceId)) {
            properties = propertiesCache.get(resourceId);
        } else {
            properties = new Properties();
            propertiesCache.put(resourceId, properties);

            try (InputStream resourceStream = resourceUrl.openStream()) {
                if (PropertyResourceFormat.XML == format) {
                    properties.loadFromXML(resourceStream);
                } else {
                    properties.load(resourceStream);
                }
            }
        }

        return properties;
    }

    String getProperty(final Properties properties, final String propertyName, final String defaultValue) {
        final String value;

        if (Property.DEFAULT_NULL.equals(defaultValue)) {
            value = properties.getProperty(propertyName);
        } else {
            value = properties.getProperty(propertyName, defaultValue);
        }

        return value;
    }

    String replaceEnvironmentReferences(final String value) {
        StringBuilder result = new StringBuilder(value.length());
        Pattern pattern = Pattern.compile("\\$\\{env\\.([_a-zA-Z0-9]+)\\}");
        Matcher m = pattern.matcher(value);
        int start = 0;

        while (m.find()) {
            String variableName = m.group(1);
            String variableValue = System.getenv(variableName);

            result.append(value.substring(start, m.start()));

            if (variableValue != null) {
                result.append(variableValue);
            }

            start = m.end();
        }

        if (start > 0) {
            result.append(value.substring(start));
        } else {
            return value;
        }

        return result.toString();
    }
}
