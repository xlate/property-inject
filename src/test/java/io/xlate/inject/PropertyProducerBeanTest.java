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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.json.Json;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@RunWith(JUnitPlatform.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PropertyProducerBeanTest {

    private PropertyProducerBean bean;

    @Mock
    PropertyResource defaultPropertyResource;

    @BeforeEach
    void setup() {
        bean = new PropertyProducerBean();
        when(defaultPropertyResource.value()).thenReturn("");
        when(defaultPropertyResource.format()).thenReturn(PropertyResourceFormat.PROPERTIES);
    }

    private Property mockProperty(String name,
                                  String resourceName,
                                  PropertyResourceFormat resourceFormat,
                                  String systemProperty,
                                  String defaultValue) {
        return mockProperty(name, "", resourceName, resourceFormat, systemProperty, defaultValue, false);
    }

    private Property mockProperty(String name,
                                  String pattern,
                                  String resourceName,
                                  PropertyResourceFormat resourceFormat,
                                  String systemProperty,
                                  String defaultValue,
                                  boolean resolveEnvironment) {

        return mockProperty(name, pattern, resourceName, resourceFormat, false, systemProperty, defaultValue, resolveEnvironment);
    }

    private Property mockProperty(String name,
                                  String pattern,
                                  String resourceName,
                                  PropertyResourceFormat resourceFormat,
                                  boolean resolveResourceEnvironment,
                                  String systemProperty,
                                  String defaultValue,
                                  boolean resolveEnvironment) {
        Property property = mock(Property.class);
        when(property.name()).thenReturn(name);
        when(property.pattern()).thenReturn(pattern);
        when(property.systemProperty()).thenReturn(systemProperty);
        when(property.defaultValue()).thenReturn(defaultValue);
        when(property.resolveEnvironment()).thenReturn(resolveEnvironment);

        when(defaultPropertyResource.value()).thenReturn(resourceName);
        when(defaultPropertyResource.format()).thenReturn(resourceFormat);
        when(defaultPropertyResource.resolveEnvironment()).thenReturn(resolveResourceEnvironment);

        when(property.resource()).thenReturn(this.defaultPropertyResource);
        return property;
    }

    private InjectionPoint mockInjectionPoint(Property property,
                                              Class<? extends Member> memberType,
                                              String memberName,
                                              int memberPosition) {
        return mockInjectionPoint(property, String.class, memberType, memberName, memberPosition);
    }

    @SuppressWarnings("unchecked")
    private InjectionPoint mockInjectionPoint(Property property,
                                              Type type,
                                              Class<? extends Member> memberType,
                                              String memberName,
                                              int memberPosition) {
        InjectionPoint injectionPoint = mock(InjectionPoint.class);

        Member member = mock(memberType);
        when(injectionPoint.getType()).thenReturn(type);
        when(injectionPoint.getMember()).thenReturn(member);
        when(member.getName()).thenReturn(memberName);

        @SuppressWarnings("rawtypes")
        Class declaringClass = getClass();
        when(member.getDeclaringClass()).thenReturn(declaringClass);

        //@SuppressWarnings("rawtypes")
        //Bean mockBean = mock(Bean.class);
        //when(injectionPoint.getBean()).thenReturn(mockBean);
        //when(mockBean.getBeanClass()).thenReturn(getClass());

        AnnotatedParameter<?> annotated = mock(AnnotatedParameter.class);
        when(annotated.getPosition()).thenReturn(memberPosition);
        when(injectionPoint.getAnnotated()).thenReturn(annotated);

        when(annotated.getAnnotation(Property.class)).thenReturn(property);

        return injectionPoint;
    }

    @Test
    void testGetPropertyForFieldMember() throws Exception {
        Property property = this.mockProperty("testGetPropertyForFieldMember",
                                              "io/xlate/inject/PropertyProducerBeanTest.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property,
                                                       String.class,
                                                       Member.class,
                                                       "testGetPropertyForFieldMember",
                                                       -1);
        String result = bean.getProperty(point);
        assertEquals("testGetPropertyForFieldMemberValue", result);
    }

    @Test
    void testGetPropertyForFieldMemberUsingFileUrl() throws Exception {
        Property property = this.mockProperty("testGetPropertyForFieldMemberUsingFileUrl",
                                              new File("target/test-classes/io/xlate/inject/PropertyProducerBeanTest.properties").toURI()
                                                                                                                                 .toURL()
                                                                                                                                 .toString(),
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property,
                                                       String.class,
                                                       Member.class,
                                                       "testGetPropertyForFieldMemberUsingFileUrl",
                                                       -1);
        String result = bean.getProperty(point);
        assertEquals("testGetPropertyForFieldMemberUsingFileUrlValue", result);
    }

    @Test
    void testGetPropertyUsingInvalidUri() throws Exception {
        Property property = this.mockProperty("irrelevant",
                                              "\n\n\n",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property,
                                                       String.class,
                                                       Member.class,
                                                       "irrelevant",
                                                       -1);

        @SuppressWarnings("unused")
        InjectionException ex = assertThrows(InjectionException.class, () -> {
            bean.getProperty(point);
        });
    }

    @Test
    void testGetPropertyUsingMalformedUrl() throws Exception {
        Property property = this.mockProperty("irrelevant",
                                              "unexpected://not/relevant/when/protocol/unknown.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property,
                                                       String.class,
                                                       Member.class,
                                                       "irrelevant",
                                                       -1);
        @SuppressWarnings("unused")
        InjectionException ex = assertThrows(InjectionException.class, () -> {
            bean.getProperty(point);
        });
    }

    @Test
    void testGetPropertyForFieldMemberWithClasspathProtocol() throws Exception {
        Property property = this.mockProperty("testGetPropertyForFieldMemberWithClasspathProtocol",
                                              "classpath:io/xlate/inject/PropertyProducerBeanTest.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property,
                                                       String.class,
                                                       Member.class,
                                                       "testGetPropertyForFieldMemberWithClasspathProtocol",
                                                       -1);
        String result = bean.getProperty(point);
        assertEquals("testGetPropertyForFieldMemberWithClasspathProtocolValue", result);
    }

    @Test
    void testGetPropertyForFieldMemberWithResourceEnvResolution() throws Exception {
        Property property = this.mockProperty("testGetPropertyForFieldMemberWithResourceEnvResolution",
                                              "",
                                              "${env.RESOURCE_LOC0}",
                                              PropertyResourceFormat.PROPERTIES,
                                              true,
                                              "",
                                              Property.DEFAULT_NULL,
                                              true);
        InjectionPoint point = this.mockInjectionPoint(property,
                                                       String.class,
                                                       Member.class,
                                                       "testGetPropertyForFieldMemberWithResourceEnvResolution",
                                                       -1);
        String result = bean.getProperty(point);
        assertEquals("testGetPropertyForFieldMemberWithResourceEnvResolutionValue", result);
    }

    @Test
    void testGetSystemPropertyForFieldMember() throws Exception {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this
                                   .mockInjectionPoint(property,
                                                       Member.class,
                                                       "testGetSystemPropertyForFieldMember",
                                                       -1);
        System.setProperty("io.xlate.inject.PropertyProducerBeanTest.testGetSystemPropertyForFieldMember", "val123");
        String result = bean.getProperty(point);
        assertEquals("val123", result);
    }

    @Test
    void testGetPropertyForFieldMemberWithDefaultResourceName() throws Exception {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this
                                   .mockInjectionPoint(property,
                                                       Member.class,
                                                       "testGetPropertyForFieldMemberWithDefaultResourceName",
                                                       -1);
        String result = bean.getProperty(point);
        assertEquals("testGetPropertyForFieldMemberWithDefaultResourceNameValue", result);
    }

    @Test
    void testGetPropertyForFieldMemberWithEnvReplacement() throws Exception {
        Property property = this.mockProperty("",
                                              "",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL,
                                              true);
        InjectionPoint point = this
                                   .mockInjectionPoint(property,
                                                       Member.class,
                                                       "testGetPropertyForFieldMemberWithEnvReplacement",
                                                       -1);
        String result = bean.getProperty(point);
        assertEquals("testGetPropertyForFieldMemberWithEnvReplacementValue" + System.getenv("INJECTED_VARIABLE"),
                     result);
    }

    /*-****************** produce String *************************/
    @Test
    void testProducePropertyString() {
        Property property = this.mockProperty("testProducePropertyString",
                                              "io/xlate/inject/PropertyProducerBeanTest.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyString", -1);
        String result = bean.produceProperty(point);
        assertEquals("testProducePropertyStringValue", result);
    }

    @Test
    void testProducePropertyStringInvalid() {
        Property property = this.mockProperty("testProducePropertyStringInvalid",
                                              "io/xlate/inject/Invalid.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyStringInvalid", -1);
        @SuppressWarnings("unused")
        InjectionException ex = assertThrows(InjectionException.class, () -> {
            bean.produceProperty(point);
        });
    }

    /*-****************** produce Boolean *************************/
    @Test
    void testProducePropertyBoolean() {
        Property property = this.mockProperty("",
                                              "io/xlate/inject/test/test.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyBoolean", -1);
        assertEquals(Boolean.TRUE, bean.produceBooleanProperty(point));
    }

    @Test
    void testProducePropertyBooleanNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Boolean.class, Member.class, "BooleanNull", -1);
        assertNull(bean.produceBooleanProperty(point));
    }

    @Test
    void testProducePropertyBooleanInvalid() {
        Property property = this.mockProperty("testProducePropertyBooleanInvalid",
                                              "io/xlate/inject/Invalid.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyBooleanInvalid", -1);
        @SuppressWarnings("unused")
        InjectionException ex = assertThrows(InjectionException.class, () -> {
            bean.produceBooleanProperty(point);
        });
    }

    /*-****************** produce native Boolean (boolean) *************************/
    @Test
    void testProducePropertyNativeBoolean() {
        Property property = this.mockProperty("",
                                              "io/xlate/inject/test/test.xml",
                                              PropertyResourceFormat.XML,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property,
                                                       int.class,
                                                       Member.class,
                                                       "testProducePropertyNativeBoolean",
                                                       -1);
        assertEquals(true, (boolean) bean.produceBooleanProperty(point));
    }

    @Test
    void testProducePropertyNativeBooleanFalse() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, boolean.class, Member.class, "BooleanNull", -1);
        assertEquals(false, (boolean) bean.produceBooleanProperty(point));
    }

    /*-****************** produce Integer *************************/
    @Test
    void testProducePropertyInteger() {
        Property property = this.mockProperty("",
                                              "io/xlate/inject/PropertyProducerBeanTest.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyInteger", -1);
        assertEquals(Integer.valueOf(42), bean.produceIntegerProperty(point));
    }

    @Test
    void testProducePropertyIntegerNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Integer.class, Member.class, "integerNull", -1);
        assertNull(bean.produceIntegerProperty(point));
    }

    @Test
    void testProducePropertyIntegerInvalid() {
        Property property = this.mockProperty("testProducePropertyIntegerInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyIntegerInvalid", -1);
        @SuppressWarnings("unused")
        InjectionException ex = assertThrows(InjectionException.class, () -> {
            bean.produceIntegerProperty(point);
        });
    }

    /*-****************** produce native Integer (int) *************************/
    @Test
    void testProducePropertyNativeInteger() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property,
                                                       int.class,
                                                       Member.class,
                                                       "testProducePropertyInteger",
                                                       -1);
        assertEquals(42, (int) bean.produceIntegerProperty(point));
    }

    @Test
    void testProducePropertyNativeIntegerZero() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, int.class, Member.class, "integerNull", -1);
        assertEquals(0, (int) bean.produceIntegerProperty(point));
    }

    /*-****************** produce Long *************************/
    @Test
    void testProducePropertyLong() {
        Property property = this.mockProperty("",
                                              "io/xlate/inject/PropertyProducerBeanTest.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property,
                                                       Long.class,
                                                       Member.class,
                                                       "testProducePropertyLong",
                                                       -1);
        assertEquals(Long.valueOf(42), bean.produceLongProperty(point));
    }

    @Test
    void testProducePropertyLongNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Long.class, Member.class, "longNull", -1);
        assertNull(bean.produceLongProperty(point));
    }

    @Test
    void testProducePropertyLongInvalid() {
        Property property = this.mockProperty("testProducePropertyLongInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property,
                                                       Long.class,
                                                       Member.class,
                                                       "testProducePropertyLongInvalid",
                                                       -1);
        @SuppressWarnings("unused")
        InjectionException ex = assertThrows(InjectionException.class, () -> {
            bean.produceLongProperty(point);
        });
    }

    /*-****************** produce native Long (long) *************************/
    @Test
    void testProducePropertyNativeLong() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property,
                                                       long.class,
                                                       Member.class,
                                                       "testProducePropertyLong",
                                                       -1);
        assertEquals(42L, (long) bean.produceLongProperty(point));
    }

    @Test
    void testProducePropertyNativeLongZero() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, long.class, Member.class, "longNull", -1);
        assertEquals(0L, (long) bean.produceLongProperty(point));
    }

    /*-****************** produce Float *************************/
    @Test
    void testProducePropertyFloat() {
        Property property = this.mockProperty("",
                                              "io/xlate/inject/PropertyProducerBeanTest.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyFloat", -1);
        assertEquals(Float.valueOf(42.0f), bean.produceFloatProperty(point));
    }

    @Test
    void testProducePropertyFloatNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "floatNull", -1);
        assertNull(bean.produceFloatProperty(point));
    }

    @Test
    void testProducePropertyFloatInvalid() {
        Property property = this.mockProperty("testProducePropertyFloatInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyFloatInvalid", -1);
        @SuppressWarnings("unused")
        InjectionException ex = assertThrows(InjectionException.class, () -> {
            bean.produceFloatProperty(point);
        });
    }

    /*-****************** produce native Float (float) *************************/
    @Test
    void testProducePropertyNativeFloat() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyFloat", -1);
        assertEquals(Float.valueOf(42.0f), bean.produceFloatProperty(point));
    }

    @Test
    void testProducePropertyNativeFloatZero() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, float.class, Member.class, "floatNull", -1);
        assertEquals(Float.valueOf(0.0f), bean.produceFloatProperty(point));
    }

    /*-****************** produce Double *************************/
    @Test
    void testProducePropertyDouble() {
        Property property = this.mockProperty("",
                                              "io/xlate/inject/PropertyProducerBeanTest.properties",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property,
                                                       float.class,
                                                       Member.class,
                                                       "testProducePropertyDouble",
                                                       -1);
        assertEquals(Double.valueOf(42.0d), bean.produceDoubleProperty(point));
    }

    @Test
    void testProducePropertyDoubleNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "DoubleNull", -1);
        assertNull(bean.produceDoubleProperty(point));
    }

    @Test
    void testProducePropertyDoubleInvalid() {
        Property property = this.mockProperty("testProducePropertyDoubleInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyDoubleInvalid", -1);
        @SuppressWarnings("unused")
        InjectionException ex = assertThrows(InjectionException.class, () -> {
            bean.produceDoubleProperty(point);
        });
    }

    /*-****************** produce native Double (double) *************************/
    @Test
    void testProducePropertyNativeDouble() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property,
                                                       double.class,
                                                       Member.class,
                                                       "testProducePropertyDouble",
                                                       -1);
        assertEquals(Double.valueOf(42.0d), bean.produceDoubleProperty(point));
    }

    @Test
    void testProducePropertyNativeDoubleZero() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, double.class, Member.class, "DoubleNull", -1);
        assertEquals(Double.valueOf(0.0d), bean.produceDoubleProperty(point));
    }

    /*-****************** produce BigDecimal *************************/
    @Test
    void testProducePropertyBigDecimal() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyBigDecimal", -1);
        assertEquals(new BigDecimal("42.042"), bean.produceBigDecimalProperty(point));
    }

    @Test
    void testProducePropertyBigDecimalNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "BigDecimalNull", -1);
        assertNull(bean.produceBigDecimalProperty(point));
    }

    @Test
    void testProducePropertyBigDecimalFormatted() {
        Property property = this.mockProperty("",
                                              "#,##0",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL,
                                              false);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "BigDecimalFormatted", -1);
        assertEquals(1_950_042.999f, bean.produceBigDecimalProperty(point).floatValue(), Float.MIN_VALUE);
    }

    @Test
    void testProducePropertyBigDecimalInvalid() {
        Property property = this.mockProperty("testProducePropertyBigDecimalInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this
                                   .mockInjectionPoint(property,
                                                       Member.class,
                                                       "testProducePropertyBigDecimalInvalid",
                                                       -1);
        @SuppressWarnings("unused")
        InjectionException ex = assertThrows(InjectionException.class, () -> {
            bean.produceBigDecimalProperty(point);
        });
    }

    @Test
    void testProducePropertyBigDecimalFormattedLoggingFine() {
        Property property = this.mockProperty("",
                                              "#,##0",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL,
                                              false);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "BigDecimalFormatted", -1);
        Logger extLogger = Logger.getLogger(bean.getClass().getName());
        extLogger.setLevel(Level.FINE);
        final List<String> messages = new ArrayList<>(0);
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                messages.add(record.getMessage());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        };
        extLogger.addHandler(handler);
        assertEquals(Float.valueOf(1_950_042.999f), bean.produceBigDecimalProperty(point).floatValue(), Float.MIN_VALUE);
        extLogger.removeHandler(handler);
        org.junit.Assert.assertEquals(0, messages.size());
    }

    /*-****************** produce BigInteger *************************/
    @Test
    void testProducePropertyBigInteger() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyBigInteger", -1);
        assertEquals(new BigInteger("-42"), bean.produceBigIntegerProperty(point));
    }

    @Test
    void testProducePropertyBigIntegerNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "BigIntegerNull", -1);
        assertNull(bean.produceBigIntegerProperty(point));
    }

    @Test
    void testProducePropertyBigIntegerFormatted() {
        Property property = this.mockProperty("",
                                              "#,##0",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL,
                                              false);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "BigIntegerFormatted", -1);
        assertEquals(1_950_042, bean.produceBigIntegerProperty(point).intValue());
    }

    @Test
    void testProducePropertyBigIntegerFormattedTruncated() {
        Property property = this.mockProperty("",
                                              "#,##0",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL,
                                              false);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "BigIntegerFormattedTruncated", -1);
        assertEquals(1_950_042, bean.produceBigIntegerProperty(point).intValue());
    }

    @Test
    void testProducePropertyBigIntegerInvalid() {
        Property property = this.mockProperty("testProducePropertyBigIntegerInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this
                                   .mockInjectionPoint(property,
                                                       Member.class,
                                                       "testProducePropertyBigIntegerInvalid",
                                                       -1);
        @SuppressWarnings("unused")
        InjectionException ex = assertThrows(InjectionException.class, () -> {
            bean.produceBigIntegerProperty(point);
        });
    }

    /*-****************** produce Date *************************/
    @Test
    void testProduceDateProperty() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProduceDateProperty", -1);
        assertEquals(LocalDateTime.parse("2017-07-01T23:45:16.432").atZone(ZoneId.of("-0400")).toInstant()
                                  .toEpochMilli(),
                     bean.produceDateProperty(point).getTime());
    }

    @Test
    void testProduceDatePropertyPattern() {
        Property property = this.mockProperty("",
                                              "M/d/yyyy H:mm:ss.SSS z",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL,
                                              false);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProduceDatePropertyPattern", -1);
        assertEquals(LocalDateTime.parse("2017-07-01T07:45:16.432").atZone(ZoneId.of("-0400")).toInstant()
                                  .toEpochMilli(),
                     bean.produceDateProperty(point).getTime());
    }

    @Test
    void testProduceDatePropertyNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "DateNull", -1);
        assertNull(bean.produceDateProperty(point));
    }

    @Test
    void testProduceDatePropertyInvalid() {
        Property property = this.mockProperty("testProduceDatePropertyInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProduceDatePropertyInvalid", -1);
        @SuppressWarnings("unused")
        InjectionException ex = assertThrows(InjectionException.class, () -> {
            bean.produceDateProperty(point);
        });
    }

    /*-****************** produce JsonArray *************************/
    @Test
    void testProducePropertyJsonArray() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyJsonArray", -1);
        assertEquals(Json.createArrayBuilder().add("elem1").add("elem2").build(), bean.produceJsonArrayProperty(point));
    }

    @Test
    void testProducePropertyJsonArrayNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "JsonArrayNull", -1);
        assertNull(bean.produceJsonArrayProperty(point));
    }

    @Test
    void testProducePropertyJsonArrayInvalid() {
        Property property = this.mockProperty("testProducePropertyJsonArrayInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this
                                   .mockInjectionPoint(property,
                                                       Member.class,
                                                       "testProducePropertyJsonArrayInvalid",
                                                       -1);
        @SuppressWarnings("unused")
        InjectionException ex = assertThrows(InjectionException.class, () -> {
            bean.produceJsonArrayProperty(point);
        });
    }

    /*-****************** produce JsonObject *************************/
    @Test
    void testProducePropertyJsonObject() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "testProducePropertyJsonObject", -1);
        assertEquals(Json.createObjectBuilder()
                         .add("key1", "value1")
                         .add("key2", 42f)
                         .add("key3",
                              Json.createArrayBuilder()
                                  .add("elem1")
                                  .add("elem2"))
                         .build(),
                     bean.produceJsonObjectProperty(point));
    }

    @Test
    void testProducePropertyJsonObjectNull() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "JsonObjectNull", -1);
        assertNull(bean.produceJsonObjectProperty(point));
    }

    @Test
    void testProducePropertyJsonObjectInvalid() {
        Property property = this.mockProperty("testProducePropertyJsonObjectInvalid",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              Property.DEFAULT_NULL);
        InjectionPoint point = this
                                   .mockInjectionPoint(property,
                                                       Member.class,
                                                       "testProducePropertyJsonObjectInvalid",
                                                       -1);
        @SuppressWarnings("unused")
        InjectionException ex = assertThrows(InjectionException.class, () -> {
            bean.produceJsonObjectProperty(point);
        });
    }

}
