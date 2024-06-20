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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.InjectionException;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.Annotated;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

@ApplicationScoped
public class PropertyResourceProducerBean {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(PropertyResourceProducerBean.class.getName());

    private final PropertyFactory factory = new PropertyFactory();

    @Inject
    private Instance<PropertyFileProvider> propertyFilenameProvider;
    
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

        Properties p = new Properties();
        try {
            URL resourceUrl = null;
            boolean hasGlobalPropertyFile =propertyFilenameProvider != null && propertyFilenameProvider.isResolvable();
            if (hasGlobalPropertyFile) {
                String globalFile = propertyFilenameProvider.get().getLocation();
                resourceUrl = factory.getResourceUrl(globalFile);
                p.putAll(factory.getProperties(resourceUrl, format, annotation.allowMissingResource()));
            }
            resourceUrl = factory.getResourceUrl(annotation, beanType);
            p.putAll(factory.getProperties(resourceUrl, format, hasGlobalPropertyFile || annotation.allowMissingResource()));
            return p;
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }

}
