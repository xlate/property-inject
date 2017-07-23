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
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CdiRunner.class)
@AdditionalClasses(PropertyInjectionExtension.class)
public class PropertyResourceProducerBeanCdiTest {

    @Inject @PropertyResource
    Properties defaultProps;

    @Inject @PropertyResource(url = "io/xlate/inject/PropertyResourceProducerBeanCdiTest2.properties")
    Properties props2;

    @Test
    public void testDefaultProps() {
        assertNotNull(defaultProps);
        assertEquals(2, defaultProps.size());
        assertEquals("val1", defaultProps.getProperty("key1"));
        assertEquals("val2", defaultProps.getProperty("key2"));
    }

    @Test
    public void testProps2() {
        assertNotNull(props2);
        assertEquals(1, props2.size());
        assertEquals("true", props2.getProperty("value.is.found"));
    }
}
