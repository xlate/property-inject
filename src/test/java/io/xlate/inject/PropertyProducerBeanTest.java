/*******************************************************************************
 * Copyright 2017 xlate.io, http://www.xlate.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.xlate.inject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Member;

import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import org.junit.Before;
import org.junit.Test;

public class PropertyProducerBeanTest {

    private PropertyProducerBean bean;

    @Before
    public void setup() {
        bean = new PropertyProducerBean();
    }

    private Property mockProperty(String name,
                                  String resourceName,
                                  PropertyResourceFormat resourceFormat,
                                  String systemProperty,
                                  String defaultValue) {
        return mockProperty(name, resourceName, resourceFormat, systemProperty, defaultValue, false);
    }

    
    private Property mockProperty(String name,
                                  String resourceName,
                                  PropertyResourceFormat resourceFormat,
                                  String systemProperty,
                                  String defaultValue,
                                  boolean resolveEnvironment) {
        Property property = mock(Property.class);
        when(property.name()).thenReturn(name);
        when(property.resourceName()).thenReturn(resourceName);
        when(property.resourceFormat()).thenReturn(resourceFormat);
        when(property.systemProperty()).thenReturn(systemProperty);
        when(property.defaultValue()).thenReturn(defaultValue);
        when(property.resolveEnvironment()).thenReturn(resolveEnvironment);
        return property;
    }

    @SuppressWarnings("unchecked")
    private InjectionPoint mockInjectionPoint(Property property,
                                              Class<? extends Member> memberType,
                                              String memberName,
                                              int memberPosition) {
        InjectionPoint injectionPoint = mock(InjectionPoint.class);

        Member member = mock(memberType);
        when(injectionPoint.getMember()).thenReturn(member);
        when(member.getName()).thenReturn(memberName);

        @SuppressWarnings("rawtypes")
        Bean mockBean = mock(Bean.class);
        when(injectionPoint.getBean()).thenReturn(mockBean);
        when(mockBean.getBeanClass()).thenReturn(getClass());

        AnnotatedParameter<?> annotated = mock(AnnotatedParameter.class);
        when (annotated.getPosition()).thenReturn(memberPosition);
        when(injectionPoint.getAnnotated()).thenReturn(annotated);

        when(annotated.getAnnotation(Property.class)).thenReturn(property);

        return injectionPoint;
    }

    @Test
    public void testGetPropertyForFieldMember() throws Exception {
        Property property = this.mockProperty("testGetPropertyForFieldMember",
                                              "io/xlate/inject/PropertyProducerBeanTest.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testGetPropertyForFieldMember", -1);
        String result = bean.getProperty(point);
        assertEquals("testGetPropertyForFieldMemberValue", result);
    }

    @Test
    public void testGetSystemPropertyForFieldMember() throws Exception {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testGetSystemPropertyForFieldMember", -1);
        System.setProperty("io.xlate.inject.PropertyProducerBeanTest.testGetSystemPropertyForFieldMember", "val123");
        String result = bean.getProperty(point);
        assertEquals("val123", result);
    }

    @Test
    public void testGetPropertyForFieldMemberWithDefaultResourceName() throws Exception {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testGetPropertyForFieldMemberWithDefaultResourceName", -1);
        String result = bean.getProperty(point);
        assertEquals("testGetPropertyForFieldMemberWithDefaultResourceNameValue", result);
    }

    @Test
    public void testGetPropertyForFieldMemberWithNullResourceName() throws Exception {
        Property property = this.mockProperty("",
                                              null,
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testGetPropertyForFieldMemberWithNullResourceName", -1);
        String result = bean.getProperty(point);
        assertEquals("testGetPropertyForFieldMemberWithNullResourceNameValue", result);
    }

    @Test
    public void testGetPropertyForFieldMemberWithEnvReplacement() throws Exception {
        Property property = this.mockProperty("",
                                              null,
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL,
                                              true);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testGetPropertyForFieldMemberWithEnvReplacement", -1);
        String result = bean.getProperty(point);
        assertEquals("testGetPropertyForFieldMemberWithEnvReplacementValue" + System.getenv("INJECTED_VARIABLE"), result);
    }

    /*-****************** produce String *************************/
    @Test
    public void testProducePropertyString() {
        Property property = this.mockProperty("testProducePropertyString",
                                              "io/xlate/inject/PropertyProducerBeanTest.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyString", -1);
        String result = bean.produceProperty(point);
        assertEquals("testProducePropertyStringValue", result);
    }

    @Test(expected = InjectionException.class)
    public void testProducePropertyStringInvalid() {
        Property property = this.mockProperty("testProducePropertyStringInvalid",
                                              "io/xlate/inject/Invalid.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyStringInvalid", -1);
        bean.produceProperty(point);
    }

    /*-****************** produce Integer *************************/
    @Test
    public void testProducePropertyInteger() {
        Property property = this.mockProperty("",
                                              "io/xlate/inject/PropertyProducerBeanTest.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyInteger", -1);
        assertEquals(Integer.valueOf(42), bean.produceIntegerProperty(point));
    }

    @Test(expected = InjectionException.class)
    public void testProducePropertyIntegerInvalid() {
        Property property = this.mockProperty("testProducePropertyIntegerInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyIntegerInvalid", -1);
        bean.produceIntegerProperty(point);
    }

}
