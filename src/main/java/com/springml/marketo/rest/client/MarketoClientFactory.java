package com.springml.marketo.rest.client;

import com.springml.marketo.rest.client.impl.LeadDatabaseClientImpl;

/**
 * Created by sam on 29/12/16.
 */
public class MarketoClientFactory {
    public static LeadDatabaseClient getLeadDatabaseClient(String clientId, String clientSecret,
                                                           String baseUri, String apiVersion) throws Exception {
        return new LeadDatabaseClientImpl(clientId, clientSecret, baseUri, apiVersion);
    }

    public static LeadDatabaseClient getLeadDatabaseClient(String clientId, String clientSecret,
                                                           String baseUri) throws Exception {
        return new LeadDatabaseClientImpl(clientId, clientSecret, baseUri);
    }
}
