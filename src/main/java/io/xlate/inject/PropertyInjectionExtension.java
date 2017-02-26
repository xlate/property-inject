package io.xlate.inject;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

public class PropertyInjectionExtension implements Extension {

	private final static Logger logger = Logger.getLogger(PropertyInjectionExtension.class.getName());

	void beforeBeanDiscovery(@Observes BeforeBeanDiscovery event, BeanManager beanManager) {
		final String method = "PropertyInjectionExtension.beforeBeanDiscovery()";

		if (logger.isLoggable(Level.FINER)) {
			logger.log(Level.FINER, method + " beanManager = " + beanManager);
		}

		final Class<?> type = PropertyProducerBean.class;
		event.addAnnotatedType(beanManager.createAnnotatedType(type));

		if (logger.isLoggable(Level.FINER)) {
			logger.log(Level.FINER, method + " added type: " + type.getName());
		}
	}
}
