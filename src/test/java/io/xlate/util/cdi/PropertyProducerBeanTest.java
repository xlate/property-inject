package io.xlate.util.cdi;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class PropertyProducerBeanTest {

	private PropertyProducerBean bean;

	@Before
	public void setup() {
		bean = new PropertyProducerBean();
	}

	@Test
	public void testPropertyProducerBean() {
		fail("Not yet implemented");
	}

	@Test
	public void testProduceProperty() {
		fail("Not yet implemented");
	}

	@Test
	public void testProduceIntegerProperty() {
		fail("Not yet implemented");
	}

	@Test
	public void testProduceLongProperty() {
		fail("Not yet implemented");
	}

	@Test
	public void testProduceFloatProperty() {
		fail("Not yet implemented");
	}

	@Test
	public void testProduceDoubleProperty() {
		fail("Not yet implemented");
	}

	@Test
	public void testProduceBigDecimalProperty() {
		fail("Not yet implemented");
	}

	@Test
	public void testProduceBigIntegerProperty() {
		fail("Not yet implemented");
	}

	@Test
	public void testProduceJsonArrayProperty() {
		fail("Not yet implemented");
	}

	@Test
	public void testProduceJsonObjectProperty() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPropertyInjectionPoint() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPropertyName() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSystemProperty() {
		fail("Not yet implemented");
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
		String defaultValue = "";
		String output = bean.getProperty(props, "someOtherKey", defaultValue);
		assertEquals(null, output);
	}

	@Test(expected = NullPointerException.class)
	public void testGetPropertyFromPropertiesFoundWithNullDefault() {
		Properties props = new Properties();
		props.setProperty("key1", "propertyValue");
		String defaultValue = null;
		String output = bean.getProperty(props, "key1", defaultValue);
		assertEquals("propertyValue", output);
	}

	@Test(expected = NullPointerException.class)
	public void testGetPropertyFromPropertiesNotFoundWithNullDefault() {
		Properties props = new Properties();
		props.setProperty("key1", "propertyValue");
		String defaultValue = null;
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
