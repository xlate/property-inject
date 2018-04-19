/*******************************************************************************
 * Copyright (C) 2018 xlate.io LLC, http://www.xlate.io
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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

    public static final String DEFAULT_NULL = "io.xlate.inject.Property.DEFAULT_NULL";

    /**
     * The name of the property. This is typically the key name in the
     * property's key-value pair. If left unspecified, the name will default to
     * the field name for Field injections or to the method name plus the
     * argument position (e.g. someMethod.arg0, someMethod.arg1...) for method
     * injections.
     *
     * Note that if the property is first found in
     * {@link java.lang.System#getProperties} (using the default name or as
     * provided in the {@link #systemProperty} parameter), it will take
     * precedence over a named property.
     *
     * @return the property name
     */
    @Nonbinding
    public String name() default "";

    // TODO: JavaDoc - used for BigDecimal and Date parsing
    @Nonbinding
    public String pattern() default "";

    /**
     * The {@link io.xlate.inject.PropertyResource} specifying the location
     * where the property given by {@link #name} can be found and the format of
     * that file. E.g. classpath:MyProperties.properties or
     * http://example.com/properties.xml. If left unspecified, the property
     * injection processor will search for a properties file on the class path
     * having the same path and name as the class where this {@link Property}
     * annotation is defined.
     *
     * Note that if the property is first found in
     * {@link java.lang.System#getProperties} (using the default name or as
     * provided in the {@link #systemProperty} parameter), it will take
     * precedence over a named property.
     *
     * @return the {@link io.xlate.inject.PropertyResource} specifying the
     *         location and format of the properties collection containing the
     *         property named by {@link #name}
     */
    @Nonbinding
    public PropertyResource resource() default @PropertyResource;

    /**
     * The name of the property to be found in
     * {@link java.lang.System#getProperties}, typically provided by the Java
     * command line. If not specified, this value will default to the
     * fully-qualified class name of the owning class, plus a '.', plus the
     * {@link #name} (either specified or defaulted).
     *
     * @return the system property name
     */
    @Nonbinding
    public String systemProperty() default "";

    /**
     * The default value of this property. If left unspecified, the default
     * value will be null for object types and the standard default value for
     * primitives, e.g. 0 for <code>int</code>.
     *
     * @return the default value
     */
    @Nonbinding
    public String defaultValue() default DEFAULT_NULL;

    /**
     * Value to indicate whether the property injection processor should replace
     * environment references in properties with the value provided by
     * {@link java.lang.System#getenv}. This allows for environment specific
     * replacement to occur.
     *
     * For example, given the following properties file content:
     *
     * <pre>
     * prop1=A literal string
     * prop2=${env.MY_ENV_VALUE} and something literal
     * </pre>
     *
     * and provided that the environment variable <code>MY_ENV_VALUE</code> is
     * set to "Something dynamic", the resolved value of <code>prop1</code> will
     * be "A literal string" and the resolved value of <code>prop2</code> will
     * be "Something dynamic and something literal".
     *
     *
     * @return true to resolve environment values, false otherwise
     */
    @Nonbinding
    public boolean resolveEnvironment() default false;
}
