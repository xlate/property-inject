/*******************************************************************************
 * Copyright 2017 xlate.io, http://www.xlate.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.xlate.inject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

@ApplicationScoped
public class PropertyProducerBean {

    private final Map<String, Properties> propertiesCache;

    public PropertyProducerBean() {
        propertiesCache = new HashMap<>();
    }

    @Produces
    @Dependent
    @Property
    public String produceProperty(InjectionPoint injectionPoint) {
        return getProperty(injectionPoint);
    }

    @Produces
    @Dependent
    @Property
    public Integer produceIntegerProperty(InjectionPoint injectionPoint) {
        try {
            return Integer.valueOf(getProperty(injectionPoint));
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public Long produceLongProperty(InjectionPoint injectionPoint) {
        try {
            return Long.valueOf(getProperty(injectionPoint));
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public Float produceFloatProperty(InjectionPoint injectionPoint) {
        try {
            return Float.valueOf(getProperty(injectionPoint));
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public Double produceDoubleProperty(InjectionPoint injectionPoint) {
        try {
            return Double.valueOf(getProperty(injectionPoint));
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public BigDecimal produceBigDecimalProperty(InjectionPoint injectionPoint) {
        try {
            return new BigDecimal(getProperty(injectionPoint));
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public BigInteger produceBigIntegerProperty(InjectionPoint injectionPoint) {
        try {
            return new BigInteger(getProperty(injectionPoint));
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public JsonArray produceJsonArrayProperty(InjectionPoint injectionPoint) {
        try {
            return Json.createReader(new StringReader(getProperty(injectionPoint))).readArray();
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public JsonObject produceJsonObjectProperty(InjectionPoint injectionPoint) {
        try {
            return Json.createReader(new StringReader(getProperty(injectionPoint))).readObject();
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    String getProperty(InjectionPoint point) {
        final Property annotation = point.getAnnotated().getAnnotation(Property.class);
        final Class<?> beanType = point.getBean().getBeanClass();
        ClassLoader loader = beanType.getClassLoader();

        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }

        final String propertyName = getPropertyName(point, annotation);
        final String defaultValue = annotation.defaultValue();

        String systemProperty = getSystemProperty(beanType, annotation, propertyName, defaultValue);

        if (systemProperty != null) {
            return systemProperty;
        }

        String resourceName = annotation.resourceName();

        if (resourceName == null || resourceName.isEmpty()) {
            resourceName = '/' + beanType.getName().replace('.', '/') + ".properties";
        }

        final Properties properties;

        if (propertiesCache.containsKey(resourceName)) {
            properties = propertiesCache.get(resourceName);
        } else {
            properties = new Properties();
            propertiesCache.put(resourceName, properties);

            try (InputStream i = loader.getResourceAsStream(resourceName)) {
                if (PropertyResourceFormat.XML == annotation.resourceFormat()) {
                    properties.loadFromXML(i);
                } else {
                    properties.load(i);
                }
            } catch (IOException e) {
                throw new InjectionException(e);
            }
        }

        return getProperty(properties, propertyName, defaultValue);
    }

    String getPropertyName(InjectionPoint point, Property annotation) {
        String propertyName = annotation.name();

        if (!propertyName.isEmpty()) {
            return propertyName;
        }

        Member member = point.getMember();

        if (member instanceof Executable) {
            Annotated annotated = point.getAnnotated();
            int p = ((AnnotatedParameter<?>) annotated).getPosition();
            propertyName = member.getName() + ".arg" + p;
        } else {
            propertyName = member.getName();
        }

        return propertyName;
    }

    String getSystemProperty(Class<?> beanType, Property annotation, String propertyName, String defaultValue) {
        String systemProperty = annotation.systemProperty();

        if (systemProperty.isEmpty()) {
            StringBuilder systemPropertyKey = new StringBuilder();
            systemPropertyKey.append(beanType.getName());
            systemPropertyKey.append('.');
            systemPropertyKey.append(propertyName);
            systemProperty = systemPropertyKey.toString();
        }

        return getProperty(System.getProperties(), systemProperty, defaultValue);
    }

    String getProperty(Properties properties, String propertyName, String defaultValue) {
        final String value;

        if (defaultValue.isEmpty()) {
            value = properties.getProperty(propertyName);
        } else {
            value = properties.getProperty(propertyName, defaultValue);
        }

        return value;
    }

    String replaceEnvironmentReferences(String value) {
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
