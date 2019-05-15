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

import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class PropertyResourceProducerBean {

    @SuppressWarnings("unused")
    private final static Logger logger = Logger.getLogger(PropertyResourceProducerBean.class.getName());

    private final PropertyFactory factory = new PropertyFactory();

    @Produces
    @Dependent
    @PropertyResource
    public Properties produceProperties(InjectionPoint point) {
        final Annotated annotated = point.getAnnotated();

        if (point.getType() != Properties.class) {
            throw new InjectionException(Properties.class + " can not be injected to type " + point.getType());
        }

        final Class<?> beanType = point.getMember().getDeclaringClass();
        final PropertyResource annotation = annotated.getAnnotation(PropertyResource.class);
        final PropertyResourceFormat format = annotation.format();
        URL resourceUrl = null;

        try {
            resourceUrl = factory.getResourceUrl(annotation, beanType);
            return factory.getProperties(resourceUrl, format, annotation.allowMissingResource());
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }
}
