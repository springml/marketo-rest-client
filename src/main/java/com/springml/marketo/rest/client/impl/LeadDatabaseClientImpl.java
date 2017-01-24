package com.springml.marketo.rest.client.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springml.marketo.rest.client.LeadDatabaseClient;
import com.springml.marketo.rest.client.model.Error;
import com.springml.marketo.rest.client.model.OAuthResponse;
import com.springml.marketo.rest.client.model.QueryResult;
import com.springml.marketo.rest.client.model.activities.ActivityTypes;
import com.springml.marketo.rest.client.model.activities.PagingToken;
import com.springml.marketo.rest.client.util.HttpHelper;
import com.springml.marketo.rest.client.util.MarketoClientException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.springml.marketo.rest.client.util.MarketoClientConstants.*;

/**
 * Implementation of {@link LeadDatabaseClient}
 */
public class LeadDatabaseClientImpl implements LeadDatabaseClient {
    private static final Logger LOG = Logger.getLogger(LeadDatabaseClient.class.getName());

    private String clientId;
    private String clientSecret;
    private String sessionId;
    private String apiVersion;
    private String baseUri;
    private ObjectMapper objectMapper;
    private HttpHelper httpHelper;

    public LeadDatabaseClientImpl(String clientId, String clientSecret, String baseUri) throws Exception {
        this(clientId, clientSecret, baseUri, STR_DEFAULT_VERSION);
    }

    public LeadDatabaseClientImpl(String clientId, String clientSecret,
                                  String baseUri, String apiVersion) throws Exception {
        validate(clientId, clientSecret, baseUri);

        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.apiVersion = apiVersion;
        this.httpHelper = new HttpHelper();
        this.baseUri = trimBaseUri(baseUri);

        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);

        sessionId = login();
    }

    public List<Map<String, String>> getAllRecords(String object) throws Exception {
        return getAllRecords(object, null);

    }

    public List<Map<String, String>> getAllRecords(String object, List<String> fields) throws Exception {
        List<Map<String, String>> totalRecords = new ArrayList<>();

        int cursor = 1;

        boolean containsMoreRecords = true;
        // TODO : What if no records found in first several iterations but still data exists?
        int retryCount = 0;

        while (containsMoreRecords || retryCount < 20) {
            // Fetch 300 records for every iteration
            StringBuilder filterValues = new StringBuilder();
            for (int i = 0; i < 300; i++) {
                filterValues.append(cursor);
                if (i != 300) {
                    filterValues.append(",");
                }

                cursor++;
            }

            QueryResult queryResult = query(object, STR_ID, filterValues.toString(), fields);
            validate(queryResult);

            if (CollectionUtils.isNotEmpty(queryResult.getResult())) {
                totalRecords.addAll(queryResult.getResult());
                containsMoreRecords = true;
            } else {
                retryCount++;
                containsMoreRecords = false;
            }
        }

        return totalRecords;
    }

    public QueryResult query(String object, String filterType, String filterValues) throws Exception {
        return query(object, filterType, filterValues, null);
    }

    public QueryResult query(String object, String filterType,
                             String filterValues, List<String> fields) throws Exception {
        return query(object, filterType, filterValues, fields, null, 0);
    }

    public QueryResult fetchNextPage(QueryResult queryResult) throws Exception {
        return query(queryResult.getObject(), queryResult.getFilterType(),
                queryResult.getFilterValues(), queryResult.getFields(),
                queryResult.getNextPageToken(), 0);
    }

    @Override
    public ActivityTypes getActivityTypes() throws Exception {
        String path = getRestPath(STR_ACTIVITIES_TYPES_PATH);
        String response = httpHelper.get(baseUri, path, sessionId);
        return objectMapper.readValue(response, ActivityTypes.class);
    }

    @Override
    public List<Map<String, String>> getActivities(String sinceDate, List<String> activityTypeIds) throws Exception {
        String path = getRestPath(OBJ_ACTIVITIES);
        return getActivities(path, sinceDate, activityTypeIds, null);
    }

    @Override
    public List<Map<String, String>> getLeadChangesActivites(String sinceDate, List<String> affectedFields) throws Exception {
        if (CollectionUtils.isEmpty(affectedFields)) {
            throw new Exception("To fetch Lead Changes, fields should be provided");
        }

        String restPath = getRestPath(STR_ACTIVITIES_LEAD_CHANGES_PATH);
        return getActivities(restPath, sinceDate, null, affectedFields);
    }

    @Override
    public List<Map<String, String>> getDeletedLeadsActivites(String sinceDate) throws Exception {
        String restPath = getRestPath(STR_ACTIVITIES_DELETED_LEADS_PATH);
        return getActivities(restPath, sinceDate, null, null);
    }

    @Override
    public String getPagingToken(String sinceDate) throws Exception {
        String restPath = getRestPath(STR_ACTIVITIES_PAGING_TOKEN_PATH);
        Map<String, String> params = new HashMap<>();
        params.put(STR_SINCE_DATE_TIME, sinceDate);

        String response = httpHelper.get(baseUri, restPath, sessionId, params);
        PagingToken pagingToken = objectMapper.readValue(response, PagingToken.class);

        return pagingToken.getNextPageToken();
    }

    private List<Map<String, String>> getActivities(String restPath,
                                                    String sinceDate,
                                                    List<String> activityTypeIds,
                                                    List<String> fields) throws Exception {
        String pagingToken = getPagingToken(sinceDate);
        LOG.info("Paging Token " + pagingToken);
        boolean containsMoreRecords = true;

        List<Map<String, String>> activities = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        if (CollectionUtils.isNotEmpty(activityTypeIds)) {
            params.put(STR_ACTIVITY_TYPE_IDS, String.join(STR_COMMA, activityTypeIds));
        }
        if (CollectionUtils.isNotEmpty(fields)) {
            params.put(STR_FIELDS, String.join(STR_COMMA, fields));
        }

        while (containsMoreRecords) {
            params.put(STR_NEXT_PAGE_TOKEN, pagingToken);

            String response = httpHelper.get(baseUri, restPath, sessionId, params);
            QueryResult queryResult = objectMapper.readValue(response, QueryResult.class);
            validate(queryResult);

            if (CollectionUtils.isNotEmpty(queryResult.getResult())) {
                activities.addAll(queryResult.getResult());
            }

            containsMoreRecords = queryResult.isMoreResult();
            pagingToken = queryResult.getNextPageToken();
        }

        return activities;
    }

    private QueryResult query(String object, String filterType,
                              String filterValues, List<String> fields,
                              String nextPageToken, int retryCount) throws Exception {
        String path = getRestPath(object);

        Map<String, String> params = new HashMap<>();
        if (StringUtils.isNotBlank(filterType)) {
            params.put(STR_FILTER_TYPE, filterType);
            params.put(STR_FILTER_VALUES, filterValues);
        }

        if (CollectionUtils.isNotEmpty(fields)) {
            params.put(STR_FIELDS, String.join(STR_COMMA, fields));
        }

        if (StringUtils.isNotBlank(nextPageToken)) {
            params.put(STR_NEXT_PAGE_TOKEN, nextPageToken);
        }

        LOG.info("Session Id : " + sessionId);
        String response = httpHelper.get(baseUri, path, sessionId, params);
        LOG.info("Response from Marketo REST Api " + response);
        QueryResult queryResult = objectMapper.readValue(response, QueryResult.class);
        if (!queryResult.isSuccess()) {
            List<Error> errors = queryResult.getErrors();
            for (Error error : errors) {
                if (retryCount < 1 &&
                        (ERROR_CODE_INVALID_ACCESS_TOKEN.equals(error.getCode()) ||
                                ERROR_CODE_EXPIRED_ACCESS_TOKEN.equals(error.getCode()))) {
                    // re-login to get new sessionId
                    login();
                    queryResult = query(object, filterType, filterValues, fields, nextPageToken, 1);
                    break;
                }
            }
        }

        queryResult.setObject(object);
        queryResult.setFields(fields);
        queryResult.setFilterType(filterType);
        queryResult.setFilterValues(filterValues);
        return queryResult;
    }

    private String getRestPath(String object) {
        return STR_SLASH + STR_REST + STR_SLASH + apiVersion + STR_SLASH + object + STR_DOT + STR_JSON;
    }

    private String login() throws Exception {
        Map<String, String> params = new HashMap<>(8);
        params.put(STR_GRANT_TYPE, STR_CLIENT_CREDENTIALS);
        params.put(STR_CLIENT_ID, clientId);
        params.put(STR_CLIENT_SECRET, clientSecret);

        String response = httpHelper.get(baseUri, STR_OAUTH_PATH, params);
        LOG.info("Auth call response  " + response);
        OAuthResponse oAuthResponse = objectMapper.readValue(response, OAuthResponse.class);
        LOG.info("Bean class for OAuthResponse" + oAuthResponse);

        return oAuthResponse.getAccessToken();
    }

    private String trimBaseUri(String baseUri) {
        if (baseUri.endsWith(STR_SLASH)) {
            baseUri= baseUri.substring(0, baseUri.length() - 1);
        }

        if (baseUri.endsWith(STR_REST)) {
            baseUri = baseUri.substring(0, baseUri.length() - 5);
        }

        return baseUri;
    }

    private void validate(String dateTime) throws Exception {
        String[] validFormat = new String[] {
                DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.toString(),
                DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.toString(),
                DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.toString()
        };

        try {
            DateUtils.parseDate(dateTime, validFormat);
        } catch (ParseException e) {
            LOG.log(Level.FINE, "Error while parsing date " + dateTime, e);
            throw new Exception("Error while parsing date " + dateTime, e);
        }
    }

    private void validate(ActivityTypes activityTypes) throws Exception {
        if (!activityTypes.getSuccess()) {
            String error = getErrorAsString(activityTypes.getErrors());
            LOG.log(Level.SEVERE, error);
            throw new Exception(error);

        }
    }

    private String getErrorAsString(List<Error> errors) {
        StringBuilder errorStmts = new StringBuilder();
        errorStmts.append("Error returned from Marketo API \n");
        for (Error error : errors) {
            errorStmts.append("Error Code : " + error.getCode() + "\n");
            errorStmts.append("Error Message : " + error.getMessage() + "\n");
            errorStmts.append("\n");
        }

        return errorStmts.toString();
    }

    private void validate(QueryResult queryResult) throws Exception {
        if (!queryResult.isSuccess()) {
            String error = getErrorAsString(queryResult.getErrors());
            LOG.log(Level.SEVERE, error);
            throw new Exception(error);
        }
    }

    private void validate(String clientId, String clientSecret, String baseUri) throws MarketoClientException {
        if (StringUtils.isBlank(clientId)) {
            throw new MarketoClientException("ClientId is mandatory to create connection with Marketo REST API");
        }

        if (StringUtils.isBlank(clientSecret)) {
            throw new MarketoClientException("ClientSecret is mandatory to create connection with Marketo REST API");
        }

        if (StringUtils.isBlank(baseUri)) {
            throw new MarketoClientException("Marketo Base Uri is mandatory to create connection with Marketo REST API");
        }
    }
}
