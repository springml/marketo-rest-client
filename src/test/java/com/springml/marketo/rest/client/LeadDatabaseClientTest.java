package com.springml.marketo.rest.client;

import com.springml.marketo.rest.client.model.QueryResult;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by sam on 29/12/16.
 */
public class LeadDatabaseClientTest {
    private static String CLIENT_ID = "55cb177c-3375-4ceb-b777-94418551b261";
    private static String CLIENT_SECRET = "UuPN6uHagXwDTOQqDtYQmjnRy6JZo4Yr";
    private static String MARKETO_BASEURI = "https://523-AGH-210.mktorest.com";

    @Test
    @Ignore
    public void testQueryLeads() throws Exception {
        LeadDatabaseClient leadDatabaseClient = MarketoClientFactory.
                getLeadDatabaseClient(CLIENT_ID, CLIENT_SECRET, MARKETO_BASEURI);

        QueryResult queryResult = leadDatabaseClient.queryObjects("leads", "Id", "1");
        System.out.println("queryResult : " + queryResult);
    }
}
