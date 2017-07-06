package io.xlate.inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.util.Properties;

import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import org.junit.Before;
import org.junit.Test;

public class PropertyFactoryTest {
    private PropertyFactory bean;

    @Before
    public void setup() {
        bean = new PropertyFactory();
    }

    private Property mockProperty(String name,
                                  String resourceName,
                                  PropertyResourceFormat resourceFormat,
                                  String systemProperty,
                                  String defaultValue) {
        Property property = mock(Property.class);
        when(property.name()).thenReturn(name);
        when(property.resourceName()).thenReturn(resourceName);
        when(property.resourceFormat()).thenReturn(resourceFormat);
        when(property.systemProperty()).thenReturn(systemProperty);
        when(property.defaultValue()).thenReturn(defaultValue);
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
        when(annotated.getPosition()).thenReturn(memberPosition);
        when(injectionPoint.getAnnotated()).thenReturn(annotated);

        when(annotated.getAnnotation(Property.class)).thenReturn(property);

        return injectionPoint;
    }

    @Test
    public void testGetPropertyNameForFieldMember() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              "");
        InjectionPoint point = this.mockInjectionPoint(property, Member.class, "field1", -1);
        String result = bean.getPropertyName(point, property.name());
        assertEquals("field1", result);
    }

    @Test
    public void testGetPropertyNameForExecutableMember() {
        Property property = this.mockProperty("",
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              "");
        InjectionPoint point = this.mockInjectionPoint(property, Executable.class, "methodName", 0);
        String result = bean.getPropertyName(point, property.name());
        assertEquals("methodName.arg0", result);
    }

    @Test
    public void testGetPropertyNameForProvidedName() {
        String name = "provided.name";
        Property property = this.mockProperty(name,
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              "");
        InjectionPoint point = this.mockInjectionPoint(property, Executable.class, "methodName", 0);
        String result = bean.getPropertyName(point, property.name());
        assertEquals(name, result);
    }

    @Test
    public void testGetSystemPropertyDefault() {
        String name = "get.system.property";
        String systemKey = getClass().getName() + '.' + name;
        Property property = this.mockProperty(name,
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              "",
                                              "");
        System.setProperty(systemKey, "theSystemValue");
        String result = bean.getSystemProperty(getClass(), property.systemProperty(), name);
        assertEquals("theSystemValue", result);
    }

    @Test
    public void testGetSystemPropertySpecified() {
        String name = "get.system.property";
        String systemKey = "getSystemPropertySpecified";
        Property property = this.mockProperty(name,
                                              "",
                                              PropertyResourceFormat.PROPERTIES,
                                              systemKey,
                                              "");
        System.setProperty(systemKey, "theSystemValue");
        String result = bean.getSystemProperty(getClass(), property.systemProperty(), name);
        assertEquals("theSystemValue", result);
    }

    @Test
    public void testGetPropertyWithClassLoader() throws IOException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final String resourceName = "io/xlate/inject/test/test.properties";
        final PropertyResourceFormat format = PropertyResourceFormat.PROPERTIES;
        final String propertyName = "testGetPropertyWithClassLoader";
        final String defaultValue = "DEFAULT";
        String output = bean.getProperty(classLoader, resourceName, format, propertyName, defaultValue);
        assertEquals("testGetPropertyWithClassLoaderValue", output);
    }

    @Test
    public void testGetPropertyFromXmlWithClassLoader() throws IOException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final String resourceName = "io/xlate/inject/test/test.xml";
        final PropertyResourceFormat format = PropertyResourceFormat.XML;
        final String propertyName = "testGetPropertyFromXmlWithClassLoader";
        final String defaultValue = "DEFAULT";
        String output = bean.getProperty(classLoader, resourceName, format, propertyName, defaultValue);
        assertEquals("testGetPropertyFromXmlWithClassLoaderValue", output);
    }

    @Test
    public void testGetPropertyFromXmlWithoutClassLoader() throws IOException {
        final ClassLoader classLoader = null;
        final String resourceName = "io/xlate/inject/test/test.xml";
        final PropertyResourceFormat format = PropertyResourceFormat.XML;
        final String propertyName = "testGetPropertyFromXmlWithoutClassLoader";
        final String defaultValue = "DEFAULT";
        String output = bean.getProperty(classLoader, resourceName, format, propertyName, defaultValue);
        assertEquals("testGetPropertyFromXmlWithoutClassLoaderValue", output);
    }

    @Test
    public void testGetPropertyMissingResourceWithClassLoader() throws IOException {
        final ClassLoader classLoader = getClass().getClassLoader();
        final String resourceName = "io/xlate/inject/test/missing.properties";
        final PropertyResourceFormat format = PropertyResourceFormat.PROPERTIES;
        final String propertyName = "";
        final String defaultValue = Property.DEFAULT_NULL;
        String output = bean.getProperty(classLoader, resourceName, format, propertyName, defaultValue);
        assertNull(output);
    }

    @Test
    public void testGetPropertyCachedWithoutClassLoader() throws IOException {
        final ClassLoader classLoader = null;
        final String resourceName = "io/xlate/inject/test/test.properties";
        final PropertyResourceFormat format = PropertyResourceFormat.PROPERTIES;
        final String propertyName = "testGetPropertyCachedWithoutClassLoader";
        final String defaultValue = "DEFAULT";
        String output = bean.getProperty(classLoader, resourceName, format, propertyName, defaultValue);
        assertEquals("testGetPropertyCachedWithoutClassLoaderValue", output);
        String output2 = bean.getProperty(classLoader, resourceName, format, propertyName, defaultValue);
        assertEquals(output, output2);
    }

    @Test
    public void testGetPropertyFromPropertiesFoundWithDefault() {
        Properties props = new Properties();
        props.setProperty("key1", "propertyValue");
        String defaultValue = "defaulted";
        String output = bean.getProperty(props, "key1", defaultValue);
        assertEquals("propertyValue", output);
    }

    @Test
    public void testGetPropertyFromPropertiesNotFoundWithDefault() {
        Properties props = new Properties();
        props.setProperty("key1", "propertyValue");
        String defaultValue = "defaulted";
        String output = bean.getProperty(props, "someOtherKey", defaultValue);
        assertEquals("defaulted", output);
    }

    @Test
    public void testGetPropertyFromPropertiesFoundWithoutDefault() {
        Properties props = new Properties();
        props.setProperty("key1", "propertyValue");
        String defaultValue = "";
        String output = bean.getProperty(props, "key1", defaultValue);
        assertEquals("propertyValue", output);
    }

    @Test
    public void testGetPropertyFromPropertiesNotFoundWithoutDefault() {
        Properties props = new Properties();
        props.setProperty("key1", "propertyValue");
        String defaultValue = Property.DEFAULT_NULL;
        String output = bean.getProperty(props, "someOtherKey", defaultValue);
        assertEquals(null, output);
    }

    @Test
    public void testGetPropertyFromPropertiesFoundWithNullDefault() {
        Properties props = new Properties();
        props.setProperty("key1", "propertyValue");
        String defaultValue = null;
        String output = bean.getProperty(props, "key1", defaultValue);
        assertEquals("propertyValue", output);
    }

    @Test
    public void testGetPropertyFromPropertiesNotFoundWithNullDefault() {
        Properties props = new Properties();
        props.setProperty("key1", "propertyValue");
        String defaultValue = Property.DEFAULT_NULL;
        String output = bean.getProperty(props, "someOtherKey", defaultValue);
        assertEquals(null, output);
    }

    @Test
    public void testReplaceEnvironmentReferences() {
        String expected = "Blah blah '" + System.getenv("INJECTED_VARIABLE") + "' bLaH blah";
        String input = "Blah blah '${env.INJECTED_VARIABLE}' bLaH blah";
        String output = bean.replaceEnvironmentReferences(input);
        assertEquals(expected, output);
    }

    @Test
    public void testReplaceEnvironmentReferencesMissing() {
        String expected = "Blah blah '' bLaH blah";
        String input = "Blah blah '${env.INJECTED_VARIABLE2}' bLaH blah";
        String output = bean.replaceEnvironmentReferences(input);
        assertEquals(expected, output);
    }

    @Test
    public void testReplaceEnvironmentReferencesInvalid() {
        String expected = "Blah blah '${INJECTED_VARIABLE2}' bLaH blah";
        String input = "Blah blah '${INJECTED_VARIABLE2}' bLaH blah";
        String output = bean.replaceEnvironmentReferences(input);
        assertEquals(expected, output);
    }

}
