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
import java.io.StringReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;

@ApplicationScoped
public class PropertyProducerBean {

    private static final Logger logger = Logger.getLogger(PropertyProducerBean.class.getName());

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
        return produceWrapped(injectionPoint, Boolean.TYPE, Boolean::valueOf, Boolean.FALSE);
    }

    @Produces
    @Dependent
    @Property
    public Integer produceIntegerProperty(InjectionPoint injectionPoint) {
        return produceWrapped(injectionPoint, Integer.TYPE, Integer::valueOf, Integer.valueOf(0));
    }

    @Produces
    @Dependent
    @Property
    public Long produceLongProperty(InjectionPoint injectionPoint) {
        return produceWrapped(injectionPoint, Long.TYPE, Long::valueOf, Long.valueOf(0L));
    }

    @Produces
    @Dependent
    @Property
    public Float produceFloatProperty(InjectionPoint injectionPoint) {
        return produceWrapped(injectionPoint, Float.TYPE, Float::valueOf, Float.valueOf(0f));
    }

    @Produces
    @Dependent
    @Property
    public Double produceDoubleProperty(InjectionPoint injectionPoint) {
        return produceWrapped(injectionPoint, Double.TYPE, Double::valueOf, Double.valueOf(0d));
    }

    @Produces
    @Dependent
    @Property
    public BigInteger produceBigIntegerProperty(InjectionPoint injectionPoint) {
        try {
            final BigDecimal value = produceBigDecimalProperty(injectionPoint);

            if (value != null) {
                return value.toBigInteger();
            }
        } catch (Exception e) {
            throw new InjectionException(e);
        }

        return null;
    }

    @Produces
    @Dependent
    @Property
    public BigDecimal produceBigDecimalProperty(InjectionPoint injectionPoint) {
        try {
            final String value = getProperty(injectionPoint);
            final BigDecimal number;

            if (value != null) {
                final Property annotation = injectionPoint.getAnnotated().getAnnotation(Property.class);
                final String pattern = annotation.pattern();

                if (pattern.isEmpty()) {
                    number = new BigDecimal(value);
                } else {
                    logger.log(Level.FINER, () -> "Parsing number with using pattern [" + pattern + ']');
                    DecimalFormat format = new DecimalFormat(pattern);
                    format.setParseBigDecimal(true);
                    number = (BigDecimal) format.parse(value);
                }
            } else {
                number = null;
            }
            return number;
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
                final Property annotation = injectionPoint.getAnnotated().getAnnotation(Property.class);
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
        return produceJson(injectionPoint, JsonReader::readArray);
    }

    @Produces
    @Dependent
    @Property
    public JsonObject produceJsonObjectProperty(InjectionPoint injectionPoint) {
        return produceJson(injectionPoint, JsonReader::readObject);
    }

    private <T> T produceWrapped(InjectionPoint injectionPoint, Class<T> type, Function<String, T> parser, T defaultValue) {
        try {
            final String value = getProperty(injectionPoint);

            if (value != null) {
                return parser.apply(value);
            }

            final Type injectionType = injectionPoint.getType();
            return injectionType.equals(type) ? defaultValue : null;
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    private <T extends JsonStructure> T produceJson(InjectionPoint injectionPoint, Function<JsonReader, T> jsonSupplier) {
        try {
            final String value = getProperty(injectionPoint);
            if (value != null) {
                try (JsonReader reader = Json.createReader(new StringReader(value))) {
                    return jsonSupplier.apply(reader);
                }
            }
            return null;
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    String getProperty(InjectionPoint point) throws IOException {
        final Property annotation = point.getAnnotated().getAnnotation(Property.class);
        final Class<?> beanType = point.getMember().getDeclaringClass();
        final String propertyName = factory.getPropertyName(point, annotation.name());
        final String defaultValue = annotation.defaultValue();

        final String systemProperty = factory.getSystemProperty(beanType,
                                                                annotation.systemProperty(),
                                                                propertyName);

        if (systemProperty != null) {
            return systemProperty;
        }

        final PropertyResource resource = annotation.resource();
        final String value;
        final URL resourceUrl = factory.getResourceUrl(resource, beanType);
        value = factory.getProperty(resourceUrl, resource.format(), resource.allowMissingResource(), propertyName, defaultValue);

        if (value != null && annotation.resolveEnvironment()) {
            return factory.replaceEnvironmentReferences(value);
        }

        return value;
    }
}
