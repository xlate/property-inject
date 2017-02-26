package io.xlate.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

@Qualifier
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {

    /**
     * The name of the property. This is typically the key name in the property's
     * key-value pair or the system property name. If left unspecified, the name
     * will default to the field name for Field injections or to the method name plus
     * the argument position (e.g. someMethod.arg0, someMethod.arg1...) for method
     * injections.
     *
     * @return the property name
     */
	@Nonbinding
	public String name() default "";

	@Nonbinding
	public String resourceName() default "";

	/**
	 *
	 *
	 * @return the <code>PropertyResourceFormat</code> of the Properties resource
	 */
	@Nonbinding
	public PropertyResourceFormat resourceFormat() default PropertyResourceFormat.PROPERTIES;

	@Nonbinding
	public String systemProperty() default "";

	@Nonbinding
	public String defaultValue() default "";

    @Nonbinding
    public boolean resolveEnvironment() default false;
}
