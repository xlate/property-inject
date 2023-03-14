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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.inject.Inject;

import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(WeldJunit5Extension.class)
class PropertyProducerBeanIT {

	@WeldSetup
	public WeldInitiator weld = WeldInitiator
		.from(PropertyProducerBean.class)
		.build();

    @Inject @Property
    String string1;

    @Inject @Property
    String string2;

    @Inject @Property(name = "string3custom")
    String string3;

    @Inject @Property(resource = @PropertyResource("io/xlate/inject/test/test.properties"))
    String string4;

    @Inject @Property(defaultValue = "string5default")
    String string5;

    @Inject @Property(systemProperty = "string6.property.name")
    String string6;

    @Inject @Property(resolveEnvironment = true)
    String string7;

    @Inject @Property(resolveEnvironment = true)
    String string8;

    @Inject
    @Property(resource = @PropertyResource(value = "missing.properties",
                                           allowMissingResource = true),
              defaultValue = "defaultString9")
    String string9;

    @Inject @Property
    int int1;

    @Inject @Property(defaultValue = "42")
    int int2;

    @Inject
    @Property
    int int3;

    @Test
    void testString1_DefaultLookup() {
        assertEquals("string1value", string1);
    }

    @Test
    void testString2_DefaultLookup_NotFound() {
        assertNull(string2);
    }

    @Test
    void testString3_OverriddenName() {
        assertEquals("string3value", string3);
    }

    @Test
    void testString4_OverriddenBundle() {
        assertEquals("string4value", string4);
    }

    @Test
    void testString5_NotFoundWithDefaultValue() {
        assertEquals("string5default", string5);
    }

    @Test
    void testString6_FoundInSystemProperties() {
        assertEquals("string6value.system", string6);
    }

    @Test
    void testString7_FoundInEnvironmentVar() {
        assertEquals("string7value.env", string7);
    }

    @Test
    void testString8_NotFound() {
        assertNull(string8);
    }

    @Test
    void testString9_MissingResourceUsesDefault() {
        assertEquals("defaultString9", string9);
    }

    @Test
    void testInt1_DefaultLookup() {
        assertEquals(42, int1);
    }

    @Test
    void testInt2_NotFoundWithDefaultValue() {
        assertEquals(42, int2);
    }

    @Test
    void testInt3_NotFoundWithPrimitiveDefaultValue() {
        assertEquals(0, int3);
    }
}
