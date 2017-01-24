package com.springml.marketo.rest.client;

import com.springml.marketo.rest.client.model.QueryResult;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static com.springml.marketo.rest.client.util.MarketoClientConstants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Junit For NamedAccounts Object
 */
public class NamedAccountsObjectTest extends BaseDatabaseClientTest {

    private String response;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        Map<String, String> params = getParamsMap("name", "Google");

        ClassLoader classLoader = getClass().getClassLoader();
        response = IOUtils.toString(classLoader.getResourceAsStream("namedAccountsResponse.json"), "UTF-8");

        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/namedaccounts.json", "e4ceb18c-f578-4ecb:sj",
                params)).thenReturn(response);
    }

    @Test
    public void testQueryNamedAccountsWithFilterType() throws Exception {

        QueryResult actualQueryResult = leadDatabaseClient.query("namedaccounts", "name",
                "Google");

        List<Map<String, String>> expectedResults = getExpectedResults();
        assertEquals(expectedResults, actualQueryResult.getResult());
    }

    @Test
    public void testQueryNamedAccountsWithFields() throws Exception {

        Map<String, String> params = getParamsMap("marketoGUID", "63980b18");
        params.put(STR_FIELDS, "name");

        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/namedaccounts.json", "e4ceb18c-f578-4ecb:sj",
                params)).thenReturn(response);

        QueryResult actualQueryResult = leadDatabaseClient.query("namedaccounts", "marketoGUID",
                "63980b18", Arrays.asList("name"));

        List<Map<String, String>> expectedResults = getExpectedResults();
        assertEquals(expectedResults, actualQueryResult.getResult());
    }

    @Test
    public void testNamedAccountsForFetchResult() throws Exception {

        QueryResult actualQueryResult = leadDatabaseClient.query("namedaccounts", "name",
                "Google");

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
        result.put("createdAt", "2017-01-03T11:01:23Z");
        result.put("name", "Google");
        result.put("marketoGUID", "63980b18");
        result.put("seq", "0");
        result.put("updatedAt", "2017-01-03T11:11:33Z");

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
