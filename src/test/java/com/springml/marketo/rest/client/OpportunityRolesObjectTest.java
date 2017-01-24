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
 * Junit For OpportunityRolesObject
 */
public class OpportunityRolesObjectTest extends BaseDatabaseClientTest {

    private String response;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        Map<String, String> params = getParamsMap("LeadId", "1");

        ClassLoader classLoader = getClass().getClassLoader();
        response = IOUtils.toString(classLoader.getResourceAsStream("opportunityRolesResponse.json"),
                "UTF-8");
        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/opportunities/roles.json",
                "e4ceb18c-f578-4ecb:sj", params)).thenReturn(response);
    }

    @Test
    public void testOpportunityRolesWithLeadId() throws Exception {

        QueryResult actualQueryResult = leadDatabaseClient.query("opportunities/roles", "LeadId",
                "1");
        List<Map<String, String>> expectedResults = getExpectedResults();
        assertEquals(expectedResults, actualQueryResult.getResult());
    }

    @Test
    public void testOpportunityRolesWithFields() throws Exception {

        Map<String, String> params = getParamsMap("marketoGUID", "37026551");
        params.put(STR_FIELDS, "LeadId");

        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/opportunities/roles.json",
                "e4ceb18c-f578-4ecb:sj", params)).thenReturn(response);

        QueryResult actualQueryResult = leadDatabaseClient.query("opportunities/roles", "marketoGUID",
                "37026551", Arrays.asList("LeadId"));

        List<Map<String, String>> expectedResults = getExpectedResults();
        assertEquals(expectedResults, actualQueryResult.getResult());
    }

    @Test
    public void testFetchResultForOpportunityRoles() throws Exception {

        QueryResult actualQueryResult = leadDatabaseClient.query("opportunities/roles", "LeadId",
                "1");
        QueryResult queryResult = getQueryResult(actualQueryResult);

        QueryResult fetchQueryResult = leadDatabaseClient.fetchNextPage(queryResult);
        List<Map<String, String>> expectedResults = getExpectedResults();
        assertEquals(expectedResults, fetchQueryResult.getResult());
    }

    private Map<String, String> getParamsMap(String filterType, String filterValue) {
        Map<String, String> params = new HashMap<>(8);
        params.put(STR_FILTER_TYPE, filterType);
        params.put(STR_FILTER_VALUES, filterValue);
        return params;
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
        result.put("createdAt", "2017-01-03T11:52:52Z");
        result.put("role", "Technical Buyer");
        result.put("externalOpportunityId", "19UYA");
        result.put("seq", "0");
        result.put("marketoGUID", "37026551");
        result.put("leadId", "1");
        result.put("updatedAt", "2017-01-03T11:52:52Z");

        List<Map<String, String>> expectedResults = new ArrayList<>();
        expectedResults.add(result);
        return expectedResults;
    }
}
