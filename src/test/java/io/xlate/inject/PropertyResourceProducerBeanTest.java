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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Member;
import java.util.Properties;

import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.spi.InjectionPoint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class PropertyResourceProducerBeanTest extends AbstractInjectionPointTest {

    private PropertyResourceProducerBean bean;

    @BeforeEach
    void setup() {
        bean = new PropertyResourceProducerBean();
    }

    private PropertyResource annotation(String url,
                                        PropertyResourceFormat format,
                                        boolean resolveEnvironment) {

        PropertyResource annotation = mock(PropertyResource.class);
        when(annotation.value()).thenReturn(url);
        when(annotation.format()).thenReturn(format);
        when(annotation.resolveEnvironment()).thenReturn(resolveEnvironment);
        return annotation;
    }

    InjectionPoint injectionPoint(PropertyResource annotation,
                                  Class<?> targetType,
                                  Class<? extends Member> memberType,
                                  String memberName,
                                  int memberPosition) {

        return super.mockInjectionPoint(PropertyResource.class,
                                        annotation,
                                        targetType,
                                        memberType,
                                        memberName,
                                        memberPosition);
    }

    @Test
    void testProducePropertiesDefault() {
        PropertyResource annotation = annotation("", PropertyResourceFormat.PROPERTIES, false);
        InjectionPoint point = injectionPoint(annotation, Properties.class, Member.class, "", -1);
        Properties result = bean.produceProperties(point);
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testProducePropertiesDefaultWrongTargetType() {
        PropertyResource annotation = annotation("", PropertyResourceFormat.PROPERTIES, false);
        InjectionPoint point = injectionPoint(annotation, getClass(), Member.class, "", -1);
        String message = "";
        try {
            bean.produceProperties(point);
        } catch (Exception e) {
            message = e.getMessage();
        }
        assertTrue(message.contains(getClass().getName()));
    }

    @Test
    void testProducePropertiesUrlNoScheme() {
        PropertyResource annotation = annotation("io/xlate/inject/test/testProducePropertiesUrl.properties",
                                                 PropertyResourceFormat.PROPERTIES,
                                                 false);
        InjectionPoint point = injectionPoint(annotation, Properties.class, Member.class, "", -1);
        Properties result = bean.produceProperties(point);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("true", result.getProperty("url.result"));
    }

    @Test
    void testProducePropertiesUrlWithScheme() {
        PropertyResource annotation = annotation("classpath:io/xlate/inject/test/testProducePropertiesUrl.properties",
                                                 PropertyResourceFormat.PROPERTIES,
                                                 false);
        InjectionPoint point = injectionPoint(annotation, Properties.class, Member.class, "", -1);
        Properties result = bean.produceProperties(point);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("true", result.getProperty("url.result"));
    }

    @Test
    void testProducePropertiesInvalidUrl() {
        PropertyResource annotation = annotation("nowhere://io/xlate/inject/test/does-not-exist!.properties",
                                                 PropertyResourceFormat.PROPERTIES,
                                                 false);
        InjectionPoint point = injectionPoint(annotation, Properties.class, Member.class, "", -1);
        @SuppressWarnings("unused")
        InjectionException ex = assertThrows(InjectionException.class, () -> {
            bean.produceProperties(point);
        });
    }

    @Test
    void testEnvironmentVarForResourceLocation() {
        PropertyResource annotation = annotation("${env.RESOURCE_LOC1}",
                                                 PropertyResourceFormat.PROPERTIES,
                                                 true);
        InjectionPoint point = injectionPoint(annotation, Properties.class, Member.class, "", -1);
        Properties result = bean.produceProperties(point);
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testProducePropertiesUnreadable() {
        File resource = new File("target/test-classes/io/xlate/inject/Unreadable.properties");
        resource.setReadable(false);
        PropertyResource annotation = annotation("classpath:io/xlate/inject/Unreadable.properties",
                                                 PropertyResourceFormat.PROPERTIES,
                                                 false);
        InjectionPoint point = injectionPoint(annotation, Properties.class, Member.class, "", -1);
        @SuppressWarnings("unused")
        InjectionException ex = assertThrows(InjectionException.class, () -> {
            bean.produceProperties(point);
        });
    }

    @Test
    void testProducePropertiesFileUrl() {
        PropertyResource annotation = annotation("file:./src/test/resources/fileprops.properties",
                                                 PropertyResourceFormat.PROPERTIES,
                                                 false);
        InjectionPoint point = injectionPoint(annotation, Properties.class, Member.class, "", -1);
        Properties result = bean.produceProperties(point);
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
