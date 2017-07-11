# property-inject [![Build Status](https://travis-ci.org/xlate/property-inject.svg?branch=master)](https://travis-ci.org/xlate/property-inject)
Simple CDI extension to support injection of java.util.Properties values

## Maven Configuration

```xml
<dependency>
	<groupId>io.xlate</groupId>
	<artifactId>property-inject</artifactId>
	<version>0.0.2</version>
</dependency>
```

If you wish to use a SNAPSHOT version, add the Sonatype OSS repository to your Maven configuration.

```xml
<repositories>
	<repository>
		<id>oss-sonatype</id>
		<name>oss-sonatype</name>
		<url>https://oss.sonatype.org/content/repositories/snapshots/</url>
		<snapshots>
			<enabled>true</enabled>
		</snapshots>
	</repository>
</repositories>
```

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

```
-Dcom.example.injection.Example.simple=theValue
```

If not found in the system properties, searches for property key 'simple' in properties file
`/com/example/injection/Example.properties` on the class path.

When neither a system property nor a matching properties file is found, the value will be set to "theDefault" as configured. If no defaultValue
is configured, the value will be `null`.
