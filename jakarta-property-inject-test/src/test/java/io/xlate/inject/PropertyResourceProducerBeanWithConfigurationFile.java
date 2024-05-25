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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Properties;

import jakarta.inject.Inject;

import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(WeldJunit5Extension.class)
class PropertyResourceProducerBeanWithConfigurationFileIT {

    @WeldSetup
    WeldInitiator weld = WeldInitiator
            .from(PropertyResourceProducerBean.class, TestFileProvider.class)
            .build();

    @Inject
    @PropertyResource
    Properties defaultProps;


    @Inject
    @PropertyResource("io/xlate/inject/PropertyResourceProducerBeanIT2.properties")
    Properties props2;

    @Test
    void testGlobalFile() {
        assertNotNull(defaultProps);
        System.out.println(defaultProps);
        assertEquals(3, defaultProps.size());
        assertEquals("x", defaultProps.getProperty("key1"));
        assertEquals("y", defaultProps.getProperty("key2"));
        assertEquals("false", defaultProps.getProperty("value.is.found"));
    }

    @Test
    void testGlobalFileOverriddenByLocalLocation() {
        assertNotNull(props2);
        System.out.println(props2);
        assertEquals(3, props2.size());
        assertEquals("true", props2.getProperty("value.is.found"));
        assertEquals("x", props2.getProperty("key1"));
        assertEquals("y", props2.getProperty("key2"));
    }


}
