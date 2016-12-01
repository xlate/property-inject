# property-inject
Simple CDI extension to support injection of java.util.Properties values

Sample usage:

```java

package property.injection;

import javax.inject.Inject;
import io.xlate.util.cdi.Property;

public class Example {

	/*
	 *  Searches for property key 'simple' in properties file
	 *  /property/injection/Example.properties on the class path.
	 *
	 *  Alternatively looks for a system property passed as to the
	 *  Java process as package + class + field.
	 *
	 *      -Dproperty.injection.Example.simple=theValue
	 *
	 *  If not found in either set of properties, the value will
	 *  be set to "theDefault" as configured.
	 */
	@Inject
	@Property(defaultValue = "theDefault")
	private String simple;



}

```