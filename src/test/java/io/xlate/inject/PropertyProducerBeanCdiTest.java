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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CdiRunner.class)
@AdditionalClasses(PropertyInjectionExtension.class)
public class PropertyProducerBeanCdiTest {

    @Inject @Property
    String string1;

    @Inject @Property
    String string2;

    @Inject @Property(name = "string3custom")
    String string3;

    @Inject @Property(resourceName = "io/xlate/inject/test/test.properties")
    String string4;

    @Inject @Property(defaultValue = "string5default")
    String string5;

    @Inject @Property(systemProperty = "string6.property.name")
    String string6;

    @Inject @Property(resolveEnvironment = true)
    String string7;

    @Inject @Property(resolveEnvironment = true)
    String string8;

    @Inject @Property
    int int1;

    @Inject @Property(defaultValue = "42")
    int int2;

    @Inject @Property
    int int3;

    @Test
    public void testString1() {
        assertEquals("string1value", string1);
    }

    @Test
    public void testString2() {
        assertNull(string2);
    }

    @Test
    public void testString3() {
        assertEquals("string3value", string3);
    }

    @Test
    public void testString4() {
        assertEquals("string4value", string4);
    }

    @Test
    public void testString5() {
        assertEquals("string5default", string5);
    }

    @Test
    public void testString6() {
        assertEquals("string6value.system", string6);
    }

    @Test
    public void testString7() {
        assertEquals("string7value.env", string7);
    }

    @Test
    public void testString8() {
        assertNull(string8);
    }

    @Test
    public void testInt1() {
        assertEquals(42, int1);
    }

    @Test
    public void testInt2() {
        assertEquals(42, int2);
    }

    @Test
    public void testInt3() {
        assertEquals(0, int3);
    }
}
