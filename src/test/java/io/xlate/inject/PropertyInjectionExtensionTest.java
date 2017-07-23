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

import org.junit.Before;
import org.junit.Test;

public class PropertyInjectionExtensionTest {

    PropertyInjectionExtension ext;
    BeforeBeanDiscovery event;
    BeanManager beanManager;

    @Before
    public void setup() {
        ext = new PropertyInjectionExtension();
        event = mock(BeforeBeanDiscovery.class);
        beanManager = mock(BeanManager.class);
    }

    @Test
    public void testBeforeBeanDiscoveryLogFiner() {
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
    public void testBeforeBeanDiscoveryLogFine() {
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
