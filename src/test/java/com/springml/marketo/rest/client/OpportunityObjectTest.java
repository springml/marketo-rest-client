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
 * Junit For Opportunity Object
 */
public class OpportunityObjectTest extends BaseDatabaseClientTest {

    private String response;
    private String responseWithFields;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        Map<String, String> params = getParamsMap();
        params.put(STR_FIELDS, "name");

        ClassLoader classLoader = getClass().getClassLoader();
        responseWithFields = IOUtils.toString(classLoader.getResourceAsStream("opportunityResponseWithFields.json"),
                "UTF-8");

        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/opportunities.json", "e4ceb18c-f578-4ecb:sj",
                params)).thenReturn(responseWithFields);
    }

    @Test
    public void testQueryOpportunitiesWithId() throws Exception {

        Map<String, String> params = getParamsMap();

        ClassLoader classLoader = getClass().getClassLoader();
        response = IOUtils.toString(classLoader.getResourceAsStream("opportunityResponse.json"), "UTF-8");

        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/opportunities.json", "e4ceb18c-f578-4ecb:sj",
                params)).thenReturn(response);

        QueryResult actualQueryResult = leadDatabaseClient.query("opportunities", "externalOpportunityId",
                "19UYA");
        List<Map<String, String>> expectedResults = getExpectedResults();
        assertEquals(expectedResults, actualQueryResult.getResult());
    }

    @Test
    public void testQueryOpportunitiesWithFields() throws Exception {

        QueryResult actualQueryResult = leadDatabaseClient.query("opportunities", "externalOpportunityId",
                "19UYA", Arrays.asList("name"));

        List<Map<String, String>> expectedResults = getExpectedResultsWithFields();
        assertEquals(expectedResults, actualQueryResult.getResult());
    }

    @Test
    public void testFetchResultForOpportunity() throws Exception {

        QueryResult actualQueryResult = leadDatabaseClient.query("opportunities", "externalOpportunityId",
                "19UYA", Arrays.asList("name"));

        QueryResult queryResult = getQueryResult(actualQueryResult);

        QueryResult fetchQueryResult = leadDatabaseClient.fetchNextPage(queryResult);
        List<Map<String, String>> expectedResults = getExpectedResultsWithFields();

        assertEquals(expectedResults, fetchQueryResult.getResult());
    }

    private Map<String, String> getParamsMap() {
        Map<String, String> params = new HashMap<>(8);
        params.put(STR_FILTER_TYPE, "externalOpportunityId");
        params.put(STR_FILTER_VALUES, "19UYA");
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

    private List<Map<String, String>> getExpectedResultsWithFields() {
        Map<String, String> result = new HashMap<>();
        result.put("name", "Linda Harrison");
        result.put("externalOpportunityId", "19UYA");
        result.put("marketoGUID", "c5df7687");
        result.put("seq", "0");

        List<Map<String, String>> expectedResults = new ArrayList<>();
        expectedResults.add(result);
        return expectedResults;
    }

    private List<Map<String, String>> getExpectedResults() {
        Map<String, String> result = new HashMap<>();
        result.put("createdAt", "2017-01-03T05:46:04Z");
        result.put("externalOpportunityId", "19UYA");
        result.put("marketoGUID", "c5df7687");
        result.put("seq", "0");
        result.put("updatedAt", "2017-01-03T09:27:54Z");

        List<Map<String, String>> expectedResults = new ArrayList<>();
        expectedResults.add(result);
        return expectedResults;
    }
}

