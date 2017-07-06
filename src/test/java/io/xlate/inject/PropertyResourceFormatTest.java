package io.xlate.inject;

import static org.junit.Assert.*;

import org.junit.Test;

public class PropertyResourceFormatTest {

    @Test
    public void testValues() {
        PropertyResourceFormat[] values = PropertyResourceFormat.values();
        assertEquals(values[0].toString(), "PROPERTIES");
        assertEquals(values[1].toString(), "XML");
    }

    @Test
    public void testValueOf() {
        assertEquals(PropertyResourceFormat.PROPERTIES, PropertyResourceFormat.valueOf("PROPERTIES"));
        assertEquals(PropertyResourceFormat.XML, PropertyResourceFormat.valueOf("XML"));
    }

}
