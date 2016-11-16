package io.xlate.util.cdi;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.InjectionPoint;

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
				properties.load(i);
			} catch (IOException e) {
				throw new InjectionException(e);
			}
		}

		return getProperty(properties, propertyName, defaultValue);
	}

	String getPropertyName(InjectionPoint point, Property annotation) {
		String propertyName = annotation.name();

		if (propertyName != null && !propertyName.isEmpty()) {
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
}
