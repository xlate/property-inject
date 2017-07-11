/*******************************************************************************
 * Copyright 2017 xlate.io, http://www.xlate.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package io.xlate.inject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.InjectionPoint;

class PropertyFactory {

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

    String getProperty(final ClassLoader classLoader,
                       final String resourceName,
                       final PropertyResourceFormat format,
                       final String propertyName,
                       final String defaultValue) throws IOException {

        final Properties properties;

        if (propertiesCache.containsKey(resourceName)) {
            properties = propertiesCache.get(resourceName);
        } else {
            properties = new Properties();
            propertiesCache.put(resourceName, properties);

            final ClassLoader loader;

            if (classLoader == null) {
                loader = ClassLoader.getSystemClassLoader();
            } else {
                loader = classLoader;
            }

            Enumeration<URL> resources = loader.getResources(resourceName);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                InputStream resourceStream = resource.openStream();

                try {
                    if (PropertyResourceFormat.XML == format) {
                        properties.loadFromXML(resourceStream);
                    } else {
                        properties.load(resourceStream);
                    }
                } finally {
                    resourceStream.close();
                }
            }
        }

        return getProperty(properties, propertyName, defaultValue);
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
