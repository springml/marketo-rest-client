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
 * Junit For CompaniesObject
 */
public class CompaniesObjectTest extends BaseDatabaseClientTest {

    private String response;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        Map<String, String> params = getParamsMap();

        ClassLoader classLoader = getClass().getClassLoader();
        response = IOUtils.toString(classLoader.getResourceAsStream("companiesResponse.json"), "UTF-8");

        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/companies.json", "e4ceb18c-f578-4ecb:sj", params)).
                thenReturn(response);
    }

    @Test
    public void testQueryCompaniesWithFilterType() throws Exception {

        Map<String, String> params = new HashMap<>(8);
        params.put(STR_FILTER_TYPE, "company");
        params.put(STR_FILTER_VALUES, "Microsoft");

        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/companies.json", "e4ceb18c-f578-4ecb:sj", params)).
                thenReturn(response);

        QueryResult actualQueryResult = leadDatabaseClient.query("companies", "company",
                "Microsoft");

        List<Map<String, String>> expectedResults = getExpectedResults();
        assertEquals(expectedResults, actualQueryResult.getResult());
    }

    @Test
    public void testQueryCompaniesWithFields() throws Exception {

        QueryResult actualQueryResult = leadDatabaseClient.query("companies", "company",
                "Microsoft", Arrays.asList("externalCompanyId"));

        List<Map<String, String>> expectedResults = getExpectedResults();
        assertEquals(expectedResults, actualQueryResult.getResult());
    }

    @Test
    public void testFetchResultsForCompanies() throws Exception {

        QueryResult actualQueryResult = leadDatabaseClient.query("companies", "company",
                "Microsoft", Arrays.asList("externalCompanyId"));
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
        queryResult.setFields(actualQueryResult.getFields());
        return queryResult;
    }

    private Map<String, String> getParamsMap() {
        Map<String, String> params = new HashMap<>(8);
        params.put(STR_FILTER_TYPE, "company");
        params.put(STR_FILTER_VALUES, "Microsoft");
        params.put(STR_FIELDS, "externalCompanyId");
        return params;
    }

    private List<Map<String, String>> getExpectedResults() {

        Map<String, String> result = new HashMap<>();
        result.put("createdAt", "2017-01-03T07:08:11Z");
        result.put("externalCompanyId", "29UYA3158");
        result.put("company", "Microsoft");
        result.put("id", "1203");
        result.put("seq", "0");
        result.put("updatedAt", "2017-01-03T07:28:51Z");

        List<Map<String, String>> expectedResults = new ArrayList<>();
        expectedResults.add(result);
        return expectedResults;
    }
}
