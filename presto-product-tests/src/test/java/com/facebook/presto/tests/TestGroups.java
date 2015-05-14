/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.tests;

public final class TestGroups
{
    public static final String CREATE_TABLE = "create_table";
    public static final String CREATE_DROP_VIEW = "create_drop_view";
    public static final String ALTER_TABLE = "alter_table";
    public static final String INDIC_BUFFERS = "indic_buffers";
    public static final String SIMPLE = "simple";
    public static final String QUARANTINE = "quarantine";
    public static final String FUNCTIONS = "functions";
    public static final String CLI = "cli";
    public static final String HIVE_CONNECTOR = "hive_connector";
    public static final String SYSTEM_CONNECTOR = "system";
    public static final String JMX_CONNECTOR = "jmx";
    public static final String SMOKE = "smoke";
    public static final String JDBC = "jdbc";

    private TestGroups() {}
}