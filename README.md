# property-inject [![Build Status](https://travis-ci.org/xlate/property-inject.svg?branch=master)](https://travis-ci.org/xlate/property-inject)
Simple CDI extension to support injection of java.util.Properties values

## Maven Configuration

```xml
<dependency>
	<groupId>io.xlate</groupId>
	<artifactId>property-inject</artifactId>
	<version>1.0.0</version>
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

### 1) Simple

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

### 2) Overridden system property name

```java
package com.example.injection;

import javax.inject.Inject;
import io.xlate.inject.Property;

public class Example {
    @Inject
    @Property(systemProperty = "example.simple")
    private String simple;
}
```
In this example, the extension searches for a system property passed to the Java process as named by the systemProperty attribute of `@Property`.

```
-Dexample.simple=theValue
```

As with the first example, if not found in the system properties, searches for property key 'simple' in properties file
`/com/example/injection/Example.properties` on the class path.

When neither a system property nor a matching properties file is found, the value will be set to  `null` since no defaultValue has been configured on the `@Property`.

### 3) Providing a properties file

```java
package com.example.injection;

import javax.inject.Inject;
import io.xlate.inject.Property;

public class Example {
    @Inject
    @Property(resourceName = "config/my-app.properties")
    private String simple;
}
```
In this example, the `resourceName` attribute of `@Property` specifies that a file named `config/my-app.properties` is available on the CLASSPATH. The properties will be loaded and the value of key `simple` injected to the field. Note that the properties will only be searched if no value is found for system property `com.example.injection.Example.simple`.

If the properties file does not exist or there is no key `simple`, the value will be set to  `null` since no defaultValue has been configured on the `@Property`.
