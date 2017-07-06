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
import java.math.BigDecimal;
import java.math.BigInteger;

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

        final String value = factory.getProperty(loader, resourceName, annotation.resourceFormat(), propertyName, defaultValue);

        if (annotation.resolveEnvironment()) {
            return factory.replaceEnvironmentReferences(value);
        }
        return value;
    }
}
