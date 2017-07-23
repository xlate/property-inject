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
public @interface PropertyResource {
    /**
     * The name of the file or resource on the class path where the property
     * give by {@link #name} can be found. E.g. MyProperties.properties. If left
     * unspecified, the property injection processor will search for a
     * properties file having the same path and name as the class where this
     * {@link PropertyResource} annotation is defined.
     *
     * @return the resource name on the class path containing the property
     */
    @Nonbinding
    public String url() default "";

    /**
     * The format of the resource named by {@link #resourceName}. Supported
     * formats are XML and Properties (key/value pairs).
     *
     * @return the <code>PropertyResourceFormat</code> of the Properties
     *         resource
     */
    @Nonbinding
    public PropertyResourceFormat format() default PropertyResourceFormat.PROPERTIES;

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
