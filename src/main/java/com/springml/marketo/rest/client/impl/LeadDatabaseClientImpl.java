package com.springml.marketo.rest.client.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springml.marketo.rest.client.LeadDatabaseClient;
import com.springml.marketo.rest.client.model.Error;
import com.springml.marketo.rest.client.model.OAuthResponse;
import com.springml.marketo.rest.client.model.QueryResult;
import com.springml.marketo.rest.client.util.HttpHelper;
import com.springml.marketo.rest.client.util.MarketoClientException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        while (containsMoreRecords) {
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
            if (!queryResult.isSuccess()) {
                throw new Exception ("Error while fetching records from Marketo. Error : " + queryResult.getErrors());
            }

            if (CollectionUtils.isNotEmpty(queryResult.getResult())) {
                totalRecords.addAll(queryResult.getResult());
            } else {
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
