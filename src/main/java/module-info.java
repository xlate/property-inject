/*******************************************************************************
 * Copyright (C) 2024 xlate.io LLC, http://www.xlate.io
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

/**
 * Inject values from java.util.Properties using the Java CDI framework
 *
 * @author Michael Edgar
 * @see <a href="https://github.com/xlate/property-inject" target="_blank">property-inject on GitHub</a>
 */
module io.xlate.inject.property {

    requires java.base;
    requires java.logging;

    requires jakarta.cdi;
    requires static jakarta.json;

    exports io.xlate.inject;

}
