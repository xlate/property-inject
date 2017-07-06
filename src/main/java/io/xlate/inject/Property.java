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
     * property's key-value pair or the system property name. If left
     * unspecified, the name will default to the field name for Field injections
     * or to the method name plus the argument position (e.g. someMethod.arg0,
     * someMethod.arg1...) for method injections.
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
     * @return the <code>PropertyResourceFormat</code> of the Properties
     *         resource
     */
    @Nonbinding
    public PropertyResourceFormat resourceFormat() default PropertyResourceFormat.PROPERTIES;

    @Nonbinding
    public String systemProperty() default "";

    @Nonbinding
    public String defaultValue() default DEFAULT_NULL;

    @Nonbinding
    public boolean resolveEnvironment() default false;
}
