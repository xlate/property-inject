# property-inject
Simple CDI extension to support injection of java.util.Properties values

## Usage

1. Simple

```java
package property.injection;

import javax.inject.Inject;
import io.xlate.util.cdi.Property;

public class Example {
    @Inject
    @Property(defaultValue = "theDefault")
    private String simple;
}

```
> In this example, the extension searches for a system property passed to the Java process as package + class + field.

>     -Dproperty.injection.Example.simple=theValue

> If not found in the system properties, searches for property key 'simple' in properties file
> `/property/injection/Example.properties` on the class path.

> When neither a system property nor a matching properties file is found, the value will be set to "theDefault" as configured. If no defaultValue
> is configured, the value will be `null`.

