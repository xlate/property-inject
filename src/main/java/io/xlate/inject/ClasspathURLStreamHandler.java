package io.xlate.inject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

class ClasspathURLStreamHandler extends URLStreamHandler {
    /** The classloader to find resources from. */
    private final ClassLoader classLoader;

    public ClasspathURLStreamHandler(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        final URL resourceUrl = classLoader.getResource(u.getPath());
        return resourceUrl.openConnection();
    }
}