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
 * Junit for SalesPersons Object
 */
public class SalesPersonsObjectTest extends BaseDatabaseClientTest {

    private String response;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        Map<String, String> params = getParamsMap();

        ClassLoader classLoader = getClass().getClassLoader();
        response = IOUtils.toString(classLoader.getResourceAsStream("salesPersonsResponse.json"),
                "UTF-8");

        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/salespersons.json",
                "e4ceb18c-f578-4ecb:sj", params)).thenReturn(response);
    }

    @Test
    public void testSalesPersonsWithFilterType() throws Exception {

        Map<String, String> params = new HashMap<>(8);
        params.put(STR_FILTER_TYPE, "externalSalesPersonId");
        params.put(STR_FILTER_VALUES, "2345dhvv");

        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/salespersons.json",
                "e4ceb18c-f578-4ecb:sj", params)).thenReturn(response);

        QueryResult actualQueryResult = leadDatabaseClient.query("salespersons", "externalSalesPersonId",
                "2345dhvv");
        List<Map<String, String>> expectedResults = getExpectedResults();
        assertEquals(expectedResults, actualQueryResult.getResult());
    }

    @Test
    public void testSalesPersonsWithfields() throws Exception {

        QueryResult actualQueryResult = leadDatabaseClient.query("salespersons", "Id", "1202",
                Arrays.asList("externalSalesPersonId"));

        List<Map<String, String>> expectedResults = getExpectedResults();
        assertEquals(expectedResults, actualQueryResult.getResult());
    }

    @Test
    public void testFetchResultForSalesPersons() throws Exception {

        QueryResult actualQueryResult = leadDatabaseClient.query("salespersons", "Id", "1202",
                Arrays.asList("externalSalesPersonId"));
        QueryResult queryResult = getQueryResult(actualQueryResult);

        List<Map<String, String>> expectedResults = getExpectedResults();
        QueryResult fetchQueryResult = leadDatabaseClient.fetchNextPage(queryResult);
        assertEquals(expectedResults, fetchQueryResult.getResult());
    }

    private Map<String, String> getParamsMap() {
        Map<String, String> params = new HashMap<>(8);
        params.put(STR_FILTER_TYPE, "Id");
        params.put(STR_FILTER_VALUES, "1202");
        params.put(STR_FIELDS, "externalSalesPersonId");
        return params;
    }

    private QueryResult getQueryResult(QueryResult actualQueryResult) {
        QueryResult queryResult = new QueryResult();
        queryResult.setObject(actualQueryResult.getObject());
        queryResult.setFilterType(actualQueryResult.getFilterType());
        queryResult.setFilterValues(actualQueryResult.getFilterValues());
        queryResult.setFields(actualQueryResult.getFields());
        return queryResult;
    }

    private List<Map<String, String>> getExpectedResults() {
        Map<String, String> result = new HashMap<>();
        result.put("createdAt", "2017-01-03T08:52:40Z");
        result.put("externalSalesPersonId", "2345dhvv");
        result.put("isEmployee", "true");
        result.put("id", "1202");
        result.put("personType", "contact");
        result.put("seq", "0");
        result.put("updatedAt", "2017-01-03T08:52:40Z");

        List<Map<String, String>> expectedResults = new ArrayList<>();
        expectedResults.add(result);
        return expectedResults;
    }
}
