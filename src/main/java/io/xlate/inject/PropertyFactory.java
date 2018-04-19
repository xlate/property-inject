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
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.InjectionPoint;

class PropertyFactory {

    private final static Logger logger = Logger.getLogger(PropertyFactory.class.getName());
    final Map<String, Properties> propertiesCache;

    PropertyFactory() {
        propertiesCache = new HashMap<>();
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

    String getProperty(final ClassLoader classLoader,
                       final String resourceName,
                       final PropertyResourceFormat format,
                       final String propertyName,
                       final String defaultValue) throws IOException {

        final Properties properties = getProperties(classLoader, resourceName, format);
        return getProperty(properties, propertyName, defaultValue);
    }

    Properties getProperties(final ClassLoader classLoader,
                             final String resourceName,
                             final PropertyResourceFormat format) throws IOException {

        final String resourceId = "classpath:" + resourceName;
        final Properties properties;

        if (propertiesCache.containsKey(resourceId)) {
            properties = propertiesCache.get(resourceId);
        } else {
            properties = new Properties();
            propertiesCache.put(resourceId, properties);

            final ClassLoader loader;

            if (classLoader == null) {
                loader = ClassLoader.getSystemClassLoader();
            } else {
                loader = classLoader;
            }

            Enumeration<URL> resources = getResources(loader, resourceName);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                try (InputStream resourceStream = resource.openStream()) {
                    if (PropertyResourceFormat.XML == format) {
                        properties.loadFromXML(resourceStream);
                    } else {
                        properties.load(resourceStream);
                    }
                }
            }
        }

        return properties;
    }

    Enumeration<URL> getResources(ClassLoader loader, String resourceName) throws IOException {
        Enumeration<URL> resources;
        URI identifier = URI.create(resourceName);

        if (identifier.getScheme() != null) {
            final URL resource = identifier.toURL();

            resources = new Enumeration<URL>() {
                boolean hasMore = true;

                @Override
                public boolean hasMoreElements() {
                    return hasMore;
                }

                @Override
                public URL nextElement() {
                    if (!hasMore) {
                        throw new NoSuchElementException();
                    }
                    hasMore = false;
                    return resource;
                }
            };
        } else {
            resources = loader.getResources(resourceName);
        }

        if (!resources.hasMoreElements()) {
            logger.log(Level.WARNING, "Resource not found by name {0}", resourceName);
        }

        return resources;
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
