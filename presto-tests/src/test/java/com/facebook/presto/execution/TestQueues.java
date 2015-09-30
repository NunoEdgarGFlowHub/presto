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
package com.facebook.presto.execution;

import com.facebook.presto.Session;
import com.facebook.presto.tests.DistributedQueryRunner;
import com.facebook.presto.tpch.TpchPlugin;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static com.facebook.presto.execution.QueryState.FAILED;
import static com.facebook.presto.execution.QueryState.QUEUED;
import static com.facebook.presto.execution.QueryState.RUNNING;
import static com.facebook.presto.testing.TestingSession.testSessionBuilder;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class TestQueues
{
    private static final Session SESSION = testSessionBuilder()
            .setCatalog("tpch")
            .setSchema("sf100000")
            .build();

    private static final Session DASHBOARD_SESSION = testSessionBuilder()
            .setCatalog("tpch")
            .setSchema("sf100000")
            .setSource("dashboard")
            .build();

    private static final String LONG_LASTING_QUERY = "SELECT COUNT(*) FROM lineitem";

    @Test(timeOut = 240_000)
    public void testSqlQueryQueueManager()
            throws Exception
    {
        Map<String, String> properties = ImmutableMap.<String, String>builder()
                .put("query.queue-config-file", getResourceFilePath("queue_config_dashboard.json"))
                .build();

        try (DistributedQueryRunner queryRunner = createQueryRunner(properties)) {
            // submit first "dashboard" query
            QueryId firstDashboardQuery = createQuery(queryRunner, DASHBOARD_SESSION, LONG_LASTING_QUERY);

            // wait for the first "dashboard" query to start
            waitForQueryState(queryRunner, firstDashboardQuery, RUNNING);

            // submit second "dashboard" query
            QueryId secondDashboardQuery = createQuery(queryRunner, DASHBOARD_SESSION, LONG_LASTING_QUERY);

            // wait for the second "dashboard" query to be queued ("dashboard.${USER}" queue strategy only allows one "dashboard" query to be accepted for execution)
            waitForQueryState(queryRunner, secondDashboardQuery, QUEUED);

            // submit first non "dashboard" query
            QueryId firstNonDashboardQuery = createQuery(queryRunner, SESSION, LONG_LASTING_QUERY);

            // wait for the first non "dashboard" query to start
            waitForQueryState(queryRunner, firstNonDashboardQuery, RUNNING);

            // submit second non "dashboard" query
            QueryId secondNonDashboardQuery = createQuery(queryRunner, SESSION, LONG_LASTING_QUERY);

            // wait for the second non "dashboard" query to be queued ("user.${USER}" queue strategy only allows three user queries to be accepted for execution,
            // two "dashboard" and one non "dashboard" queries are already accepted by "user.${USER}" queue)
            waitForQueryState(queryRunner, secondNonDashboardQuery, QUEUED);

            // cancel first "dashboard" query, second "dashboard" query and second non "dashboard" query should start running
            cancelQuery(queryRunner, firstDashboardQuery);
            waitForQueryState(queryRunner, firstDashboardQuery, FAILED);
            waitForQueryState(queryRunner, secondDashboardQuery, RUNNING);
            waitForQueryState(queryRunner, secondNonDashboardQuery, RUNNING);
        }
    }

    @Test(timeOut = 240_000)
    public void testSqlQueryQueueManagerWithTwoDashboardQueriesRequestedAtTheSameTime()
            throws Exception
    {
        Map<String, String> properties = ImmutableMap.<String, String>builder()
                .put("query.queue-config-file", getResourceFilePath("queue_config_dashboard.json"))
                .build();

        try (DistributedQueryRunner queryRunner = createQueryRunner(properties)) {
            QueryId firstDashboardQuery = createQuery(queryRunner, DASHBOARD_SESSION, LONG_LASTING_QUERY);
            QueryId secondDashboardQuery = createQuery(queryRunner, DASHBOARD_SESSION, LONG_LASTING_QUERY);

            waitForQueryState(queryRunner, firstDashboardQuery, ImmutableList.of(QUEUED, RUNNING));
            waitForQueryState(queryRunner, secondDashboardQuery, ImmutableList.of(QUEUED, RUNNING));
        }
    }

    @Test(timeOut = 240_000)
    public void testSqlQueryQueueManagerWithTooManyQueriesScheduled()
            throws Exception
    {
        Map<String, String> properties = ImmutableMap.<String, String>builder()
                .put("query.queue-config-file", getResourceFilePath("queue_config_dashboard.json"))
                .build();

        try (DistributedQueryRunner queryRunner = createQueryRunner(properties)) {
            QueryId firstDashboardQuery = createQuery(queryRunner, DASHBOARD_SESSION, LONG_LASTING_QUERY);
            waitForQueryState(queryRunner, firstDashboardQuery, RUNNING);

            QueryId secondDashboardQuery = createQuery(queryRunner, DASHBOARD_SESSION, LONG_LASTING_QUERY);
            waitForQueryState(queryRunner, secondDashboardQuery, QUEUED);

            QueryId thirdDashboardQuery = createQuery(queryRunner, DASHBOARD_SESSION, LONG_LASTING_QUERY);
            waitForQueryState(queryRunner, thirdDashboardQuery, FAILED);
        }
    }

    private static QueryId createQuery(DistributedQueryRunner queryRunner, Session session, String sql)
    {
        return queryRunner.getCoordinator().getQueryManager().createQuery(session, sql).getQueryId();
    }

    private static void cancelQuery(DistributedQueryRunner queryRunner, QueryId queryId)
    {
        queryRunner.getCoordinator().getQueryManager().cancelQuery(queryId);
    }

    private static void waitForQueryState(DistributedQueryRunner queryRunner, QueryId queryId, QueryState expectedQueryState)
            throws InterruptedException
    {
        waitForQueryState(queryRunner, queryId, ImmutableList.of(expectedQueryState));
    }

    private static void waitForQueryState(DistributedQueryRunner queryRunner, QueryId queryId, List<QueryState> expectedQueryStates)
            throws InterruptedException
    {
        QueryManager queryManager = queryRunner.getCoordinator().getQueryManager();
        do {
            MILLISECONDS.sleep(500);
        }
        while (!expectedQueryStates.contains(queryManager.getQueryInfo(queryId).getState()));
    }

    private String getResourceFilePath(String fileName)
    {
        return this.getClass().getClassLoader().getResource(fileName).getPath();
    }

    private static DistributedQueryRunner createQueryRunner(Map<String, String> properties)
            throws Exception
    {
        DistributedQueryRunner queryRunner = new DistributedQueryRunner(testSessionBuilder().build(), 2, properties);

        try {
            queryRunner.installPlugin(new TpchPlugin());
            queryRunner.createCatalog("tpch", "tpch");
            return queryRunner;
        }
        catch (Exception e) {
            queryRunner.close();
            throw e;
        }
    }
}
