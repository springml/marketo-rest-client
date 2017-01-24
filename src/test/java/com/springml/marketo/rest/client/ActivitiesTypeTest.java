package com.springml.marketo.rest.client;

import com.springml.marketo.rest.client.model.QueryResult;
import com.springml.marketo.rest.client.model.activities.ActivityTypes;
import com.springml.marketo.rest.client.model.activities.Attribute;
import com.springml.marketo.rest.client.model.activities.PrimaryAttribute;
import com.springml.marketo.rest.client.model.activities.Result;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static com.springml.marketo.rest.client.util.MarketoClientConstants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Junit for ActivityTypes
 */
public class ActivitiesTypeTest extends BaseDatabaseClientTest {

    private String response;
    private String PagingTokenResponse;
    private Date date;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        ClassLoader classLoader = getClass().getClassLoader();
        PagingTokenResponse = IOUtils.toString(classLoader.getResourceAsStream("pagingTokenResponse.json"),
                "UTF-8");

        Map<String, String> tokenParams = new HashMap<>();
        date = new Date();
        tokenParams.put(STR_SINCE_DATE_TIME, String.valueOf(date));
        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/activities/pagingtoken.json", "e4ceb18c-f578-4ecb:sj",
                tokenParams)).thenReturn(PagingTokenResponse);

        Map<String, String> params = new HashMap<>(8);
        params.put(STR_FILTER_TYPE, STR_ACTIVITY_TYPE_IDS);
        params.put(STR_FILTER_VALUES, "12");

        response = IOUtils.toString(classLoader.getResourceAsStream("activitiesResponse.json"), "UTF-8");

        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/activities.json", "e4ceb18c-f578-4ecb:sj", params)).
                thenReturn(response);
    }

    @Test
    public void testQueryActivitiesWithFilterValues() throws Exception {

        QueryResult actualQueryResult = leadDatabaseClient.query("activities", STR_ACTIVITY_TYPE_IDS,
                "12");

        List<Map<String, String>> expectedResults = getExpectedResults();
        assertEquals(expectedResults, actualQueryResult.getResult());
    }

    @Test
    public void testQueryActivitiesWithFilterValuesAndFields() throws Exception {

        Map<String, String> params = new HashMap<>(8);
        params.put(STR_FILTER_TYPE, STR_ACTIVITY_TYPE_IDS);
        params.put(STR_FILTER_VALUES, "12");
        params.put(STR_FIELDS, STR_NEXT_PAGE_TOKEN);

        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/activities.json", "e4ceb18c-f578-4ecb:sj", params)).
                thenReturn(response);

        List<Map<String, String>> expectedResults = getExpectedResults();

        QueryResult actualQueryResult = leadDatabaseClient.query("activities", STR_ACTIVITY_TYPE_IDS,
                "12", Arrays.asList(STR_NEXT_PAGE_TOKEN));
        assertEquals(expectedResults, actualQueryResult.getResult());
    }

    @Test
    public void testGetActivitiesTypes() throws Exception {

        ClassLoader classLoader = getClass().getClassLoader();
        String activityTypesResponse = IOUtils.toString(classLoader.getResourceAsStream("activitiesTypesResponse.json"),
                "UTF-8");

        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/activities/types.json", "e4ceb18c-f578-4ecb:sj")).
                thenReturn(activityTypesResponse);

        ActivityTypes activityTypes = leadDatabaseClient.getActivityTypes();

        List<Result> results = getExpectedActivitiesResults();
        assertEquals(results, activityTypes.getResult());
    }

    @Test
    public void testActivities() throws Exception {

        Map<String, String> params = new HashMap<>();
        StringBuilder filterValueUpto300 = getStringBuilderUpto300(1);
        params.put(STR_ACTIVITY_TYPE_IDS, String.valueOf(filterValueUpto300));
        params.put(STR_NEXT_PAGE_TOKEN, PAGING_TOKEN);

        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/activities.json", "e4ceb18c-f578-4ecb:sj", params)).
                thenReturn(response);
        List<String> ids = new ArrayList<>();
        ids.add(String.valueOf(filterValueUpto300));

        List<Map<String, String>> actualActivities = leadDatabaseClient.getActivities(String.valueOf(date), ids);
        List<Map<String, String>> expectedActivities = getExpectedResults();

        assertEquals(expectedActivities, actualActivities);
    }

    @Test
    public void testActivitiesLeadChanges() throws Exception {

        Map<String, String> params = new HashMap<>(8);
        params.put(STR_NEXT_PAGE_TOKEN, PAGING_TOKEN);
        params.put(STR_FIELDS, "firstName");

        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/activities/leadchanges.json", "e4ceb18c-f578-4ecb:sj",
                params)).thenReturn(response);

        List<Map<String, String>> actualActivities = leadDatabaseClient.getLeadChangesActivites(String.valueOf(date),
                Arrays.asList("firstName"));
        List<Map<String, String>> expectedActivities = getExpectedResults();

        assertEquals(expectedActivities, actualActivities);
    }

    @Test
    public void testActivitiesDeletedLeads() throws Exception {

        Map<String, String> params = new HashMap<>(8);
        params.put(STR_NEXT_PAGE_TOKEN, PAGING_TOKEN);

        when(httpHelper.get(MARKETO_BASEURI, "/rest/v1/activities/deletedleads.json", "e4ceb18c-f578-4ecb:sj",
                params)).thenReturn(response);

        List<Map<String, String>> actualActivities = leadDatabaseClient.getDeletedLeadsActivites(String.valueOf(date));
        List<Map<String, String>> expectedActivities = getExpectedResults();

        assertEquals(expectedActivities, actualActivities);
    }

    private List<Result> getExpectedActivitiesResults() {

        Result result = new Result();
        result.setId(1);
        result.setName("Add to Nurture");
        result.setDescription("Add a lead to a nurture program");

        PrimaryAttribute primaryAttribute = new PrimaryAttribute();
        primaryAttribute.setName("Program ID");
        primaryAttribute.setDataType("integer");
        result.setPrimaryAttribute(primaryAttribute);

        List<Attribute> attributes = new ArrayList<>();
        Attribute attribute = new Attribute();
        attribute.setName("Track ID");
        attribute.setDataType("integer");
        attributes.add(attribute);
        result.setAttributes(attributes);

        List<Result> results = new ArrayList<>(32);
        results.add(result);
        return results;
    }

    private List<Map<String, String>> getExpectedResults() {
        List<Map<String, String>> results = new ArrayList<>();

        Map<String, String> result = new HashMap<>(8);
        result.put("activityDate", "2016-12-27T06:02:18Z");
        result.put("attributes.Created Date", "2016-12-27");
        result.put("id", "1");
        result.put("attributes.Source Type", "New lead");
        result.put("primaryAttributeValue", "Sam Alex");
        result.put("primaryAttributeValueId", "1");
        result.put("leadId", "1");
        result.put("activityTypeId", "12");

        results.add(result);
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
}
