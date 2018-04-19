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
public @interface PropertyResource {
    /**
     * The URL specifying the location of a resource in properties format. If
     * left unspecified, the property injection processor will search for a
     * properties file on the class path having the same name as the class where
     * this {@link PropertyResource} annotation is defined, including package
     * name. If no protocol is specified, the class path will be searched.
     *
     * @return URL specifying the location of resource(s) in properties format
     */
    @Nonbinding
    public String value() default "";

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
