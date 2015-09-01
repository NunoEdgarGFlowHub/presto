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
import com.teradata.tempto.ProductTest;
import com.teradata.tempto.context.ThreadLocalTestContextHolder;
import com.teradata.tempto.query.QueryResult;

import java.sql.SQLException;

import static com.teradata.tempto.query.QueryExecutor.query;
import static java.lang.String.format;

public abstract class HivePartitioningTest
        extends ProductTest
{
    protected long getProcessedLinesCount(String sqlStatement, QueryResult queryResult)
            throws SQLException
    {
        QueryInfoClient queryInfoClient = ThreadLocalTestContextHolder.testContext().getDependency(QueryInfoClient.class);
        String queryId;
        if (queryResult.getJdbcResultSet().isPresent() && queryResult.getJdbcResultSet().get().isWrapperFor(PrestoResultSet.class)) {
            // if PrestoResult is available, just unwrap it from ResultSet and extract query id
            queryId = queryResult.getJdbcResultSet().get().unwrap(PrestoResultSet.class).getQueryId();
        }
        else {
            // if there is no ResultSet (UPDATE statements), try to find it in system.runtime.queries table
            queryId = (String) query(format("select query_id from system.runtime.queries where query = '%s'", sqlStatement)).row(0).get(0);
        }
        return queryInfoClient.getQueryStats(queryId).get().getRawInputPositions();
    }
}
