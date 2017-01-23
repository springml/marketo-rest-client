    package com.springml.marketo.rest.client;

    import com.springml.marketo.rest.client.model.QueryResult;
    import org.apache.commons.io.IOUtils;
    import org.junit.Before;
    import org.junit.Test;

    import java.util.*;

    import static com.springml.marketo.rest.client.util.MarketoClientConstants.*;
    import static org.junit.Assert.assertEquals;
    import static org.mockito.Mockito.when;

    /**
     * Created by sam on 29/12/16.
     */

    public class LeadDatabaseClientTest extends BaseDatabaseClientTest {

        private String response;
        private String EmptyResponse;

        @Before
        public void setUp() throws Exception {
            super.setUp();

            Map<String, String> params = getParamsMap("10");

            ClassLoader classLoader = getClass().getClassLoader();
            response = IOUtils.toString(classLoader.getResourceAsStream("leadResponse.json"), "UTF-8");
            EmptyResponse = IOUtils.toString(classLoader.getResourceAsStream("leadEmptyResponse.json"),
                    "UTF-8");

            when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/leads.json", "e4ceb18c-f578-4ecb:sj", params)).
                    thenReturn(response);
        }

        @Test
        public void testQueryLeads() throws Exception {

            Map<String, String> params = new HashMap<>(8);
            params.put(STR_FILTER_TYPE, "Id");
            params.put(STR_FILTER_VALUES, "10");

            when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/leads.json", "e4ceb18c-f578-4ecb:sj", params)).
                    thenReturn(response);

            List<Map<String, String>> expectedResults = getExpectedResultMaps();

            QueryResult actualQueryResult = leadDatabaseClient.query("leads", "Id", "10");
            assertEquals(expectedResults, actualQueryResult.getResult());
        }

        @Test
        public void testQueryLeadsWithFields() throws Exception {

            QueryResult actualQueryResult = leadDatabaseClient.query("leads", "Id", "10",
                    Arrays.asList("email"));

            List<Map<String, String>> expectedResults = getExpectedResultMaps();
            assertEquals(expectedResults, actualQueryResult.getResult());
        }

        @Test
        public void testQueryLeadsForAllRecords() throws Exception {

            Map<String, String> paramsUpto300 = new HashMap<>();
            paramsUpto300.put(STR_FILTER_TYPE, "Id");
            StringBuilder filterValueUpto300 = getStringBuilderUpto300(1);
            paramsUpto300.put(STR_FILTER_VALUES, String.valueOf(filterValueUpto300));

            Map<String, String> paramsUpto600 = new HashMap<>();
            paramsUpto600.put(STR_FILTER_TYPE, "Id");
            StringBuilder filterValueUpto600 = getStringBuilderUpto600(301);
            paramsUpto600.put(STR_FILTER_VALUES, String.valueOf(filterValueUpto600));

            when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/leads.json", "e4ceb18c-f578-4ecb:sj", paramsUpto300)).
                    thenReturn(response);

            when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/leads.json", "e4ceb18c-f578-4ecb:sj", paramsUpto600)).
                    thenReturn(EmptyResponse);

            List<Map<String, String>> actualQueryResults = leadDatabaseClient.getAllRecords("leads");

            List<Map<String, String>> expectedResults = getExpectedResultMaps();

            assertEquals(expectedResults, actualQueryResults);

        }

        @Test
        public void testQueryLeadsForAllRecordsWithFields() throws Exception {

            Map<String, String> paramsUpto300 = new HashMap<>();
            paramsUpto300.put(STR_FILTER_TYPE, "Id");
            StringBuilder filterValueUpto300 = getStringBuilderUpto300(1);
            paramsUpto300.put(STR_FILTER_VALUES, String.valueOf(filterValueUpto300));
            paramsUpto300.put(STR_FIELDS, "email");

            Map<String, String> paramsUpto600 = new HashMap<>();
            paramsUpto600.put(STR_FILTER_TYPE, "Id");
            StringBuilder filterValueUpto600 = getStringBuilderUpto600(301);
            paramsUpto600.put(STR_FILTER_VALUES, String.valueOf(filterValueUpto600));
            paramsUpto600.put(STR_FIELDS, "email");

            when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/leads.json", "e4ceb18c-f578-4ecb:sj", paramsUpto300)).
                    thenReturn(response);

            when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/leads.json", "e4ceb18c-f578-4ecb:sj", paramsUpto600)).
                    thenReturn(EmptyResponse);

            List<Map<String, String>> actualQueryResults = leadDatabaseClient.getAllRecords("leads", Arrays.asList("email"));
            List<Map<String, String>> expectedResults = getExpectedResultMaps();

            assertEquals(expectedResults, actualQueryResults);
        }

        @Test
        public void testFetchResultsForLeads() throws Exception {

            Map<String, String> params = getParamsMap("1,2");
            ClassLoader classLoader = getClass().getClassLoader();
            response = IOUtils.toString(classLoader.getResourceAsStream("leadFetchPageResponse.json"),
                    "UTF-8");
            when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/leads.json", "e4ceb18c-f578-4ecb:sj", params)).
                    thenReturn(response);

            QueryResult actualQueryResult = leadDatabaseClient.query("leads", "Id", "1,2",
                    Arrays.asList("email"));

            QueryResult queryResult = getQueryResult(actualQueryResult);
            Map<String, String> pageTokenParams = getParamsMap("1,2");
            pageTokenParams.put(STR_NEXT_PAGE_TOKEN, PAGING_TOKEN);
            response = IOUtils.toString(classLoader.getResourceAsStream("leadFetchPageResponse.json"),
                    "UTF-8");
            when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/leads.json", "e4ceb18c-f578-4ecb:sj",
                    pageTokenParams)).thenReturn(response);

            QueryResult fetchQueryResult = leadDatabaseClient.fetchNextPage(queryResult);
            List<Map<String, String>> results = getMapsForFetchResult();
            assertEquals(results, fetchQueryResult.getResult());
        }

        private QueryResult getQueryResult(QueryResult actualQueryResult) {
            QueryResult queryResult = new QueryResult();
            queryResult.setObject(actualQueryResult.getObject());
            queryResult.setFilterType(actualQueryResult.getFilterType());
            queryResult.setFilterValues(actualQueryResult.getFilterValues());
            queryResult.setFields(actualQueryResult.getFields());
            queryResult.setNextPageToken(actualQueryResult.getNextPageToken());
            return queryResult;
        }

        private List<Map<String, String>> getMapsForFetchResult() {
            Map<String, String> result1 = new HashMap<>();
            result1.put("id", "1");
            result1.put("updatedAt", "2016-12-27T06:02:18Z");
            result1.put("lastName", "Alex");
            result1.put("email", "sam@test.com");
            result1.put("createdAt", "2016-12-27T06:02:18Z");
            result1.put("firstName", "Sam");

            Map<String, String> result2 = new HashMap<>();
            result2.put("id", "2");
            result2.put("updatedAt", "2016-12-30T05:25:29Z");
            result2.put("lastName", "Wright");
            result2.put("email", "rwrighty@csmonitor.com");
            result2.put("createdAt", "2016-12-30T05:25:29Z");
            result2.put("firstName", "Robin");

            List<Map<String, String>> results = new ArrayList<>();
            results.add(result1);
            results.add(result2);
            return results;
        }

        private StringBuilder getStringBuilderUpto300(int count) {
            StringBuilder filterValueUpto300 = new StringBuilder();
            for (int i = 0; i < 300; i++) {
                filterValueUpto300.append(count);
                if (i != 299) {
                    filterValueUpto300.append(",");
                }

                count++;
            }
            return filterValueUpto300;
        }

        private StringBuilder getStringBuilderUpto600(int count) {
            StringBuilder filterValueUpto600 = new StringBuilder();
            for (int i = 300; i < 600; i++) {
                filterValueUpto600.append(count);
                if (i != 599) {
                    filterValueUpto600.append(",");
                }

                count++;
            }
            return filterValueUpto600;
        }

        private List<Map<String, String>> getExpectedResultMaps() {
            Map<String, String> result = new HashMap<>();

            result.put("id", "10");
            result.put("updatedAt", "2016-12-27T06:02:18Z");
            result.put("lastName", "Alex");
            result.put("email", "sam@test.com");
            result.put("createdAt", "2016-12-27T06:02:18Z");
            result.put("firstName", "Sam");

            return Arrays.asList(result);
        }

        private Map<String, String> getParamsMap(String value) {
            Map<String, String> params = new HashMap<>(8);
            params.put(STR_FILTER_TYPE, "Id");
            params.put(STR_FILTER_VALUES, value);
            params.put(STR_FIELDS, "email");
            return params;
        }
    }


