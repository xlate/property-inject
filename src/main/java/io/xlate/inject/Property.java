/*******************************************************************************
 * Copyright 2017 xlate.io, http://www.xlate.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
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

    // TODO: JavaDoc
    @Nonbinding
    public String pattern() default "";

    /**
     * The name of the file or resource on the class path where the property
     * give by {@link #name} can be found. E.g. MyProperties.properties. If left
     * unspecified, the property injection processor will search for a
     * properties file having the same path and name as the class where this
     * {@link Property} annotation is defined.
     *
     * Note that if the property is first found in
     * {@link java.lang.System#getProperties} (using the default name or as
     * provided in the {@link #systemProperty} parameter), it will take
     * precedence over a named property.
     *
     * @return the resource name on the class path containing the property
     */
    @Nonbinding
    public String resourceName() default "";

    /**
     * The format of the resource named by {@link #resourceName}. Supported
     * formats are XML and Properties (key/value pairs).
     *
     * @return the <code>PropertyResourceFormat</code> of the Properties
     *         resource
     */
    @Nonbinding
    public PropertyResourceFormat resourceFormat() default PropertyResourceFormat.PROPERTIES;

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
