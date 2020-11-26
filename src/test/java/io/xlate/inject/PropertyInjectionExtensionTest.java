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

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class PropertyInjectionExtensionTest {

    PropertyInjectionExtension ext;
    BeforeBeanDiscovery event;
    BeanManager beanManager;

    @BeforeEach
    void setup() {
        ext = new PropertyInjectionExtension();
        event = mock(BeforeBeanDiscovery.class);
        beanManager = mock(BeanManager.class);
    }

    @Test
    void testBeforeBeanDiscoveryLogFiner() {
        Logger extLogger = Logger.getLogger(ext.getClass().getName());
        extLogger.setLevel(Level.FINER);
        final List<String> messages = new ArrayList<>(2);
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                messages.add(record.getMessage());
            }
            @Override
            public void flush() {}
            @Override
            public void close() throws SecurityException {}
        };
        extLogger.addHandler(handler);
        ext.beforeBeanDiscovery(event, beanManager);
        extLogger.removeHandler(handler);
        org.junit.Assert.assertEquals(3, messages.size());
    }

    @Test
    void testBeforeBeanDiscoveryLogFine() {
        Logger extLogger = Logger.getLogger(ext.getClass().getName());
        extLogger.setLevel(Level.FINE);
        final List<String> messages = new ArrayList<>(0);
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                messages.add(record.getMessage());
            }
            @Override
            public void flush() {}
            @Override
            public void close() throws SecurityException {}
        };
        extLogger.addHandler(handler);
        ext.beforeBeanDiscovery(event, beanManager);
        extLogger.removeHandler(handler);
        org.junit.Assert.assertEquals(0, messages.size());
    }
}
