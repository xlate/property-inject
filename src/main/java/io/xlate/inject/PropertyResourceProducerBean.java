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

        final Class<?> beanType = point.getBean().getBeanClass();
        final ClassLoader loader = beanType.getClassLoader();
        final PropertyResource annotation = annotated.getAnnotation(PropertyResource.class);
        final PropertyResourceFormat format = annotation.format();

        String locator = annotation.url();

        if (locator.isEmpty()) {
            locator = beanType.getName().replace('.', '/') + ".properties";
        }

        try {
            return factory.getProperties(loader, locator, format);
        } catch (Exception e) {
            throw new InjectionException(e);
        }
    }
}
