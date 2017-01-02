package com.springml.marketo.rest.client;

import com.springml.marketo.rest.client.model.QueryResult;

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

}
