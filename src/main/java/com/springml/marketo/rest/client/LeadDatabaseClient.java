package com.springml.marketo.rest.client;

import com.springml.marketo.rest.client.model.QueryResult;
import com.springml.marketo.rest.client.model.activities.ActivityTypes;

import java.util.List;
import java.util.Map;

/**
 * Java API to execute the Marketo Lead Database REST API
 */
public interface LeadDatabaseClient {

    /**
     * Returns all records with all fields of the specified object
     * @param object Object for which all records to be fetched (Like leads)
     * @return
     */
    List<Map<String, String>> getAllRecords(String object) throws Exception;

    /**
     * Returns all records of the specified object
     * @param object Object for which all records to be fetched (Like leads)
     * @param fields Fields to be queried from the object
     * @return
     */
    List<Map<String, String>> getAllRecords(String object, List<String> fields) throws Exception;

    /**
     * Query Lead database objects
     * @param object Object to be queried (Like leads)
     * @param filterType Field to be used for filter
     * @param filterValues Values to be used in filter
     * @return
     */
    QueryResult query(String object, String filterType, String filterValues) throws Exception;

    /**
     * Query Lead database objects
     * @param object Object to be queried (Like leads)
     * @param filterType Field to be used for filter
     * @param filterValues Values to be used in filter
     * @param fields Fields to be queried from the object
     * @return
     */
    QueryResult query(String object, String filterType, String filterValues, List<String> fields) throws Exception;

    /**
     * Fetches Next Page if exists
     * @param queryResult
     * @return
     * @throws Exception
     */
    QueryResult fetchNextPage(QueryResult queryResult) throws Exception;

    /**
     * List all the activity types using /rest/v1/activities/types.json
     * @return
     * @throws Exception
     */
    ActivityTypes getActivityTypes() throws Exception;

    /**
     * List all the activities of the specified types
     * @param sinceDate
     * @param activityTypeIds
     * @return
     * @throws Exception
     */
    List<Map<String, String>> getActivities(String sinceDate, List<String> activityTypeIds) throws Exception;

    /**
     * List Lead changes
     * @param sinceDate
     * @param affectedFields List of fields you want to retrieve changes for
     * @return
     * @throws Exception
     */
    List<Map<String, String>> getLeadChangesActivites(String sinceDate, List<String> affectedFields) throws Exception;

    /**
     * Get the activities for Deleted Leads
     * @param sinceDate
     * @return
     * @throws Exception
     */
    List<Map<String, String>> getDeletedLeadsActivites(String sinceDate) throws Exception;

    /**
     * Get the paging token to be used on other calls
     * @param sinceDateTime
     * @return
     * @throws Exception
     */
    String getPagingToken(String sinceDateTime) throws Exception;
}
