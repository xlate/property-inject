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

import java.io.StringReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

@ApplicationScoped
public class PropertyProducerBean {

    private final PropertyFactory factory = new PropertyFactory();

    @Produces
    @Dependent
    @Property
    public String produceProperty(InjectionPoint injectionPoint) {
        try {
            return getProperty(injectionPoint);
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public Boolean produceBooleanProperty(InjectionPoint injectionPoint) {
        try {
            final String value = getProperty(injectionPoint);

            if (value != null) {
                return Boolean.valueOf(value);
            }

            final Type type = injectionPoint.getType();
            return type.equals(boolean.class) ? Boolean.FALSE : null;
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public Integer produceIntegerProperty(InjectionPoint injectionPoint) {
        try {
            final String value = getProperty(injectionPoint);

            if (value != null) {
                return Integer.valueOf(value);
            }

            final Type type = injectionPoint.getType();
            return type.equals(int.class) ? Integer.valueOf(0) : null;
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public Long produceLongProperty(InjectionPoint injectionPoint) {
        try {
            final String value = getProperty(injectionPoint);

            if (value != null) {
                return Long.valueOf(value);
            }

            final Type type = injectionPoint.getType();
            return type.equals(long.class) ? Long.valueOf(0L) : null;
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public Float produceFloatProperty(InjectionPoint injectionPoint) {
        try {
            final String value = getProperty(injectionPoint);
            return value != null ? Float.valueOf(value) : null;
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public float produceNativeFloatProperty(InjectionPoint injectionPoint) {
        // TODO: move to float property
        final Float value = produceFloatProperty(injectionPoint);
        return value != null ? value.floatValue() : 0f;
    }

    @Produces
    @Dependent
    @Property
    public Double produceDoubleProperty(InjectionPoint injectionPoint) {
        try {
            final String value = getProperty(injectionPoint);
            return value != null ? Double.valueOf(value) : null;
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public double produceNativeDoubleProperty(InjectionPoint injectionPoint) {
        // TODO: move to double property
        final Double value = produceDoubleProperty(injectionPoint);
        return value != null ? value.doubleValue() : 0d;
    }

    @Produces
    @Dependent
    @Property
    public BigDecimal produceBigDecimalProperty(InjectionPoint injectionPoint) {
        // TODO: Decimal Format
        try {
            final String value = getProperty(injectionPoint);
            return value != null ? new BigDecimal(value) : null;
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public BigInteger produceBigIntegerProperty(InjectionPoint injectionPoint) {
        // TODO: Decimal Format
        try {
            final String value = getProperty(injectionPoint);
            return value != null ? new BigInteger(value) : null;
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public Date produceDateProperty(InjectionPoint injectionPoint) {
        try {
            final String value = getProperty(injectionPoint);
            final Date date;
            if (value != null) {
                final Property annotation = injectionPoint.getAnnotated()
                                                          .getAnnotation(Property.class);
                final String pattern = annotation.pattern();
                DateFormat format = new SimpleDateFormat(pattern.isEmpty() ? "yyyy-MM-dd'T'HH:mm:ss.SSSZ" : pattern);
                date = format.parse(value);
            } else {
                date = null;
            }
            return date;
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public JsonArray produceJsonArrayProperty(InjectionPoint injectionPoint) {
        try {
            final String value = getProperty(injectionPoint);
            return value != null ? Json.createReader(new StringReader(value)).readArray() : null;
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public JsonObject produceJsonObjectProperty(InjectionPoint injectionPoint) {
        try {
            final String value = getProperty(injectionPoint);
            return value != null ? Json.createReader(new StringReader(value)).readObject() : null;
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    String getProperty(InjectionPoint point) throws Exception {
        final Property annotation = point.getAnnotated().getAnnotation(Property.class);
        final Class<?> beanType = point.getBean().getBeanClass();
        final ClassLoader loader = beanType.getClassLoader();
        final String propertyName = factory.getPropertyName(point, annotation.name());
        final String defaultValue = annotation.defaultValue();

        final String systemProperty = factory.getSystemProperty(beanType,
                                                                annotation.systemProperty(),
                                                                propertyName);

        if (systemProperty != null) {
            return systemProperty;
        }

        String resourceName = annotation.resourceName();

        if (resourceName == null || resourceName.isEmpty()) {
            resourceName = beanType.getName().replace('.', '/') + ".properties";
        }

        final String value = factory
                .getProperty(loader, resourceName, annotation.resourceFormat(), propertyName, defaultValue);

        if (value != null && annotation.resolveEnvironment()) {
            return factory.replaceEnvironmentReferences(value);
        }
        return value;
    }
}
