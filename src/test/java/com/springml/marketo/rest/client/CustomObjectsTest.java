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
 * Junit For CustomObjects Object
 */
public class CustomObjectsTest extends BaseDatabaseClientTest {
    private String response;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        Map<String, String> params = getParamsMap("testField", "testValue1");

        ClassLoader classLoader = getClass().getClassLoader();
        response = IOUtils.toString(classLoader.getResourceAsStream("customObjectsResponse.json"), "UTF-8");

        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/customobjects/myCustomObject_c.json",
                "e4ceb18c-f578-4ecb:sj", params)).thenReturn(response);
    }

    @Test
    public void testQueryNamedAccountsWithFilterType() throws Exception {

        QueryResult actualQueryResult = leadDatabaseClient.query("customobjects/myCustomObject_c",
                "testField", "testValue1");

        List<Map<String, String>> expectedResults = getExpectedResults();
        assertEquals(expectedResults, actualQueryResult.getResult());
    }

    @Test
    public void testQueryNamedAccountsWithFields() throws Exception {

        Map<String, String> params = getParamsMap("testField", "testValue1");
        params.put(STR_FIELDS, "marketoGUID");

        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/customobjects/myCustomObject_c.json",
                "e4ceb18c-f578-4ecb:sj", params)).thenReturn(response);

        QueryResult actualQueryResult = leadDatabaseClient.query("customobjects/myCustomObject_c",
                "testField", "testValue1", Arrays.asList("marketoGUID"));

        List<Map<String, String>> expectedResults = getExpectedResults();
        assertEquals(expectedResults, actualQueryResult.getResult());
    }

    @Test
    public void testNamedAccountsForFetchResult() throws Exception {

        QueryResult actualQueryResult = leadDatabaseClient.query("customobjects/myCustomObject_c",
                "testField", "testValue1");

        QueryResult queryResult = getQueryResult(actualQueryResult);
        QueryResult fetchQueryResult = leadDatabaseClient.fetchNextPage(queryResult);

        List<Map<String, String>> expectedResults = getExpectedResults();
        assertEquals(expectedResults, fetchQueryResult.getResult());
    }

    private QueryResult getQueryResult(QueryResult actualQueryResult) {
        QueryResult queryResult = new QueryResult();
        queryResult.setObject(actualQueryResult.getObject());
        queryResult.setFilterType(actualQueryResult.getFilterType());
        queryResult.setFilterValues(actualQueryResult.getFilterValues());
        return queryResult;
    }

    private List<Map<String, String>> getExpectedResults() {
        Map<String, String> result = new HashMap<>();
        result.put("seq", "0");
        result.put("marketoGUID", "8693d1a2");
        result.put("testField", "testValue1");
        result.put("updatedAt", "2017-01-20T11:36:26Z");
        result.put("createdAt", "2017-01-20T11:36:26Z");

        List<Map<String, String>> expectedResults = new ArrayList<>();
        expectedResults.add(result);
        return expectedResults;
    }

    private Map<String, String> getParamsMap(String filterType, String filterValue) {
        Map<String, String> params = new HashMap<>(8);
        params.put(STR_FILTER_TYPE, filterType);
        params.put(STR_FILTER_VALUES, filterValue);
        return params;
    }
}
