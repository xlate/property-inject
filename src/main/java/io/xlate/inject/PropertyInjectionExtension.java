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

        addAnnotatedType(event, beanManager, PropertyProducerBean.class);
        addAnnotatedType(event, beanManager, PropertyResourceProducerBean.class);
    }

    void addAnnotatedType(BeforeBeanDiscovery event, BeanManager beanManager, Class<?> type) {
        final String method = "PropertyInjectionExtension.addAnnotatedType()";

        event.addAnnotatedType(beanManager.createAnnotatedType(type));

        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, method + " added type: " + type.getName());
        }
    }
}
