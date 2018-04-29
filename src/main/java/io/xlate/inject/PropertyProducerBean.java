/*******************************************************************************
 * Copyright (C) 2018 xlate.io LLC, http://www.xlate.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package io.xlate.inject;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLStreamHandler;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

@ApplicationScoped
public class PropertyProducerBean {

    private final static Logger logger = Logger.getLogger(PropertyProducerBean.class.getName());

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
        Boolean property = null;

        try {
            final String value = getProperty(injectionPoint);

            if (value != null) {
                property = Boolean.valueOf(value);
            } else if (boolean.class.equals(injectionPoint.getType())) {
                property = Boolean.FALSE;
            }
        } catch (Exception e) {
            throw new InjectionException(e);
        }

        return property;
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

            if (value != null) {
                return Float.valueOf(value);
            }

            final Type type = injectionPoint.getType();
            return type.equals(float.class) ? Float.valueOf(0f) : null;
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

    @Produces
    @Dependent
    @Property
    public Double produceDoubleProperty(InjectionPoint injectionPoint) {
        try {
            final String value = getProperty(injectionPoint);

            if (value != null) {
                return Double.valueOf(value);
            }

            final Type type = injectionPoint.getType();
            return type.equals(double.class) ? Double.valueOf(0d) : null;
        } catch (Exception e) {
            throw new InjectionException(e);
        }
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
                    if (logger.isLoggable(Level.FINER)) {
                        logger.log(Level.FINER, "Parsing number with using pattern [" + pattern + ']');
                    }

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

        final PropertyResource resource = annotation.resource();
        final String location = resource.value();
        final URLStreamHandler handler = new ClasspathURLStreamHandler(loader);
        final String value;
        final URL resourceUrl;

        if (location.isEmpty()) {
            StringBuilder resourceName = new StringBuilder();
            resourceName.append("classpath:");
            resourceName.append(beanType.getName().replace('.', '/'));
            resourceName.append(".properties");

            resourceUrl = new URL(null, resourceName.toString(), handler);
        } else {
            final String resolvedLocation;

            if (resource.resolveEnvironment()) {
                resolvedLocation = factory.replaceEnvironmentReferences(location);
            } else {
                resolvedLocation = location;
            }

            try {
                final URI resourceId = URI.create(resolvedLocation);
                final String scheme = resourceId.getScheme();

                if (scheme != null) {
                    if ("classpath".equals(scheme)) {
                        resourceUrl = new URL(null, resolvedLocation, handler);
                    } else {
                        resourceUrl = resourceId.toURL();
                    }
                } else {
                    resourceUrl = new URL(null, "classpath:" + location, handler);
                }
            } catch (IllegalArgumentException | MalformedURLException e) {
                throw new InjectionException(e);
            }
        }

        value = factory.getProperty(resourceUrl, resource.format(), propertyName, defaultValue);

        if (value != null && annotation.resolveEnvironment()) {
            return factory.replaceEnvironmentReferences(value);
        }

        return value;
    }
}
