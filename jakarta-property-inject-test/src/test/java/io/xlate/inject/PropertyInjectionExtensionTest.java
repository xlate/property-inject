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
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PropertyInjectionExtensionTest {

    PropertyInjectionExtension ext;
    BeforeBeanDiscovery        event;
    BeanManager                beanManager;

    @BeforeEach
    void setup() {
        ext = new PropertyInjectionExtension();
        event = mock(BeforeBeanDiscovery.class);
        beanManager = mock(BeanManager.class);

    }

    @Test
    void testBeforeBeanDiscoveryLogFiner() {
        final Logger extLogger = Logger.getLogger(ext.getClass().getName());
        extLogger.setLevel(Level.FINER);
        final List<String> messages = new ArrayList<>(2);
        final Handler      handler  = new Handler() {
                                        @Override
                                        public void publish(final LogRecord record) {
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
        ext.beforeBeanDiscovery(event, beanManager);
        extLogger.removeHandler(handler);
        assertEquals(3, messages.size());
    }

    @Test
    void testBeforeBeanDiscoveryLogFine() {
        final Logger extLogger = Logger.getLogger(ext.getClass().getName());
        extLogger.setLevel(Level.FINE);
        final List<String> messages = new ArrayList<>(0);
        final Handler      handler  = new Handler() {
                                        @Override
                                        public void publish(final LogRecord record) {
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
        ext.beforeBeanDiscovery(event, beanManager);
        extLogger.removeHandler(handler);
        assertEquals(0, messages.size());
    }
}
