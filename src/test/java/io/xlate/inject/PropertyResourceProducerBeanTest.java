package io.xlate.inject;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Member;
import java.util.Properties;

import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.spi.InjectionPoint;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PropertyResourceProducerBeanTest extends AbstractInjectionPointTest {

    private PropertyResourceProducerBean bean;

    @Before
    public void setup() {
        bean = new PropertyResourceProducerBean();
    }

    private PropertyResource annotation(String url,
                                        PropertyResourceFormat format,
                                        boolean resolveEnvironment) {

        PropertyResource annotation = mock(PropertyResource.class);
        when(annotation.url()).thenReturn(url);
        when(annotation.format()).thenReturn(format);
        when(annotation.resolveEnvironment()).thenReturn(resolveEnvironment);
        return annotation;
    }

    InjectionPoint injectionPoint(PropertyResource annotation,
                                  Class<?> targetType,
                                  Class<? extends Member> memberType,
                                  String memberName,
                                  int memberPosition) {

        return super.mockInjectionPoint(PropertyResource.class,
                                        annotation,
                                        targetType,
                                        memberType,
                                        memberName,
                                        memberPosition);
    }

    @Test
    public void testProducePropertiesDefault() {
        PropertyResource annotation = annotation("", PropertyResourceFormat.PROPERTIES, false);
        InjectionPoint point = injectionPoint(annotation, Properties.class, Member.class, "", -1);
        Properties result = bean.produceProperties(point);
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
    }

    @Test
    public void testProducePropertiesDefaultWrongTargetType() {
        PropertyResource annotation = annotation("", PropertyResourceFormat.PROPERTIES, false);
        InjectionPoint point = injectionPoint(annotation, getClass(), Member.class, "", -1);
        String message = "";
        try {
            bean.produceProperties(point);
        } catch (Exception e) {
            message = e.getMessage();
        }
        Assert.assertTrue(message.contains(getClass().getName()));
    }

    @Test
    public void testProducePropertiesUrl() {
        PropertyResource annotation = annotation("io/xlate/inject/test/testProducePropertiesUrl.properties",
                                                 PropertyResourceFormat.PROPERTIES,
                                                 false);
        InjectionPoint point = injectionPoint(annotation, Properties.class, Member.class, "", -1);
        Properties result = bean.produceProperties(point);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("true", result.getProperty("url.result"));
    }

    @Test(expected = InjectionException.class)
    public void testProducePropertiesInvalidUrl() {
        PropertyResource annotation = annotation("nowhere://io/xlate/inject/test/does-not-exist!.properties",
                                                 PropertyResourceFormat.PROPERTIES,
                                                 false);
        InjectionPoint point = injectionPoint(annotation, Properties.class, Member.class, "", -1);
        bean.produceProperties(point);
    }

}
