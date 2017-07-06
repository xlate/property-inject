# property-inject [![Build Status](https://travis-ci.org/xlate/property-inject.svg?branch=master)](https://travis-ci.org/xlate/property-inject)
Simple CDI extension to support injection of java.util.Properties values

## Usage

1. Simple

```java
package com.example.injection;

import javax.inject.Inject;
import io.xlate.inject.Property;

public class Example {
    @Inject
    @Property(defaultValue = "theDefault")
    private String simple;
}

```
In this example, the extension searches for a system property passed to the Java process as package + class + field.

> `-Dcom.example.injection.Example.simple=theValue`

If not found in the system properties, searches for property key 'simple' in properties file
`/com/example/injection/Example.properties` on the class path.

When neither a system property nor a matching properties file is found, the value will be set to "theDefault" as configured. If no defaultValue
is configured, the value will be `null`.
