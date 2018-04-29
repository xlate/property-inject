package io.xlate.inject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import javax.enterprise.inject.InjectionException;

class ClasspathURLStreamHandler extends URLStreamHandler {
    /** The classloader to find resources from. */
    private final ClassLoader classLoader;

    public ClasspathURLStreamHandler(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    protected URLConnection openConnection(URL u) {
        final String resourcePath = u.getPath();
        final URL resourceUrl = classLoader.getResource(resourcePath);

        if (resourceUrl == null) {
            throw new InjectionException("Class-path resource not found: " + resourcePath);
        }

        try {
            return resourceUrl.openConnection();
        } catch (IOException e) {
            throw new InjectionException(e);
        }
    }
}