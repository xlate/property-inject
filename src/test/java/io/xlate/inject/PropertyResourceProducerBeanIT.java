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
public class PropertyResourceProducerBeanIT {

    @Inject
    @PropertyResource
    Properties defaultProps;

    @Inject
    @PropertyResource("io/xlate/inject/PropertyResourceProducerBeanIT2.properties")
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
