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

    public QueryResult queryObjects(String object) {
        return null;
    }

    public QueryResult queryObjects(String object, List<String> fields) {
        return null;
    }

    public QueryResult queryObjects(String object, String filterType, String filterValues) throws Exception {
        return queryObjects(object, filterType, filterValues, null);
    }

    public QueryResult queryObjects(String object, String filterType,
                                    String filterValues, List<String> fields) throws Exception {
        return queryObjects(object, filterType, filterValues, fields, 0);
    }

    private QueryResult queryObjects(String object, String filterType,
                                     String filterValues, List<String> fields,
                                     int retryCount) throws Exception {
        String path = getRestPath(object);

        Map<String, String> params = new HashMap<>();
        if (StringUtils.isNotBlank(filterType)) {
            params.put(STR_FILTER_TYPE, filterType);
            params.put(STR_FILTER_VALUES, filterValues);
        }

        if (CollectionUtils.isNotEmpty(fields)) {
            params.put(STR_FIELDS, String.join(STR_COMMA, fields));
        }

        LOG.info("Session Id : " + sessionId);
        String response = httpHelper.get(baseUri, path, sessionId, params);
        LOG.info("Response from Marketo REST Api " + response);
        QueryResult queryResult = objectMapper.readValue(response, QueryResult.class);
        if (!queryResult.isSuccess()) {
            List<Error> errors = queryResult.getErrors();
            for (Error error : errors) {
                if (retryCount < 1 && ERROR_CODE_INVALID_AUTH_TOKEN.equals(error.getCode())) {
                    // re-login to get new sessionId
                    login();
                    return queryObjects(object, filterType, filterValues, fields, 1);
                }
            }
        }

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
