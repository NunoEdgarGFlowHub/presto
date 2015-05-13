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

package com.facebook.presto.tests.hive;

import com.facebook.presto.jdbc.PrestoResultSet;
import com.facebook.presto.tests.queryinfo.QueryInfoClient;
import com.google.inject.Inject;
import com.teradata.test.ProductTest;
import com.teradata.test.Requirement;
import com.teradata.test.RequirementsProvider;
import com.teradata.test.fulfillment.hive.DataSource;
import com.teradata.test.fulfillment.hive.HiveTableDefinition;
import com.teradata.test.fulfillment.table.MutableTablesState;
import com.teradata.test.query.QueryResult;
import org.testng.annotations.Test;

import java.sql.SQLException;

import static com.facebook.presto.tests.TestGroups.HIVE_CONNECTOR;
import static com.teradata.test.Requirements.allOf;
import static com.teradata.test.assertions.QueryAssert.Row.row;
import static com.teradata.test.assertions.QueryAssert.assertThat;
import static com.teradata.test.fulfillment.hive.InlineDataSource.createResourceDataSource;
import static com.teradata.test.fulfillment.table.MutableTableRequirement.State.LOADED;
import static com.teradata.test.fulfillment.table.TableRequirements.mutableTable;
import static com.teradata.test.query.QueryExecutor.query;
import static org.assertj.core.api.Assertions.assertThat;

public class TestTablePartitioningWithHiveConnector
        extends ProductTest
        implements RequirementsProvider
{
    private static final HiveTableDefinition SINGLE_INT_COLUMN_PARTITIONEND_TEXTFILE = singleIntColumnPartitionedTableDefinition("TEXTFILE");
    private static final HiveTableDefinition SINGLE_INT_COLUMN_PARTITIONED_ORC = singleIntColumnPartitionedTableDefinition("ORC");
    private static final HiveTableDefinition SINGLE_INT_COLUMN_PARTITIONED_RCFILE = singleIntColumnPartitionedTableDefinition("RCFILE");
    private static final HiveTableDefinition SINGLE_INT_COLUMN_PARTITIONED_PARQUET = singleIntColumnPartitionedTableDefinition("PARQUET");
    private static final String TABLE_NAME = "test_table";

    @Inject
    private MutableTablesState tablesState;
    @Inject
    private QueryInfoClient queryInfoClient;

    private static HiveTableDefinition singleIntColumnPartitionedTableDefinition(String fileFormat)
    {
        String tableName = fileFormat.toLowerCase() + "_single_int_column_partitioned";
        DataSource dataSource = createResourceDataSource(tableName, "" + System.currentTimeMillis(), "com/facebook/presto/tests/hive/data/single_int_column/data." + fileFormat.toLowerCase());
        return HiveTableDefinition.builder()
                .setName(tableName)
                .setCreateTableDDLTemplate("" +
                        "CREATE EXTERNAL TABLE %NAME%(" +
                        "   col INT" +
                        ") " +
                        "PARTITIONED BY (part_col INT) " +
                        "ROW FORMAT DELIMITED FIELDS TERMINATED BY '|' " +
                        "STORED AS " + fileFormat)
                .addPartition("part_col = 1", dataSource)
                .addPartition("part_col = 2", dataSource)
                .build();
    }

    @Override
    public Requirement getRequirements()
    {
        return allOf(
                mutableTable(SINGLE_INT_COLUMN_PARTITIONEND_TEXTFILE, TABLE_NAME, LOADED),
                mutableTable(SINGLE_INT_COLUMN_PARTITIONED_ORC, TABLE_NAME, LOADED),
                mutableTable(SINGLE_INT_COLUMN_PARTITIONED_RCFILE, TABLE_NAME, LOADED),
                mutableTable(SINGLE_INT_COLUMN_PARTITIONED_PARQUET, TABLE_NAME, LOADED));
    }

    @Test(groups = HIVE_CONNECTOR)
    public void testSelectPartitionedHiveTableTextFile()
            throws SQLException
    {
        String tableNameInDatabase = tablesState.get(TABLE_NAME).getNameInDatabase();

        String selectFromAllPartitionsSql = "SELECT * FROM " + tableNameInDatabase;
        QueryResult allPartitionsQueryResult = query(selectFromAllPartitionsSql);
        assertThat(allPartitionsQueryResult).containsOnly(row(42, 1), row(42, 2));
        int numberOfTasksForAllPartitions = tasksCountFor(allPartitionsQueryResult);
        assertThat(numberOfTasksForAllPartitions).isGreaterThan(0);

        String selectFromOnePartitionsSql = "SELECT * FROM " + tableNameInDatabase + " WHERE part_col = 2";
        QueryResult onePartitionQueryResult = query(selectFromOnePartitionsSql);
        assertThat(onePartitionQueryResult).containsOnly(row(42, 2));
        int numberOfTasksForOnePartition = tasksCountFor(onePartitionQueryResult);
        assertThat(numberOfTasksForAllPartitions).isGreaterThan(numberOfTasksForOnePartition);
    }

    private int tasksCountFor(QueryResult queryResult)
            throws SQLException
    {
        PrestoResultSet prestoResultSet = queryResult.getJdbcResultSet().get().unwrap(PrestoResultSet.class);
        return queryInfoClient.getQueryStats(prestoResultSet.getQueryId()).get().getCompletedTasks();
    }
}
