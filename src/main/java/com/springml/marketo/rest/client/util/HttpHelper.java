package com.springml.marketo.rest.client.util;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.springml.marketo.rest.client.util.MarketoClientConstants.STR_UTF_8;

/**
 * Utility Class to access Marketo REST API
 */
public class HttpHelper {
    private static final Logger LOG = Logger.getLogger(HttpHelper.class.getName());

    public String get(String baseUri, String path, Map<String, String> params) throws Exception {
        return get(baseUri, path, params, null);
    }

    public String get(String baseUri, String path, String sessionId,
                      Map<String, String> params) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sessionId);

        return get(baseUri, path, params, headers);
    }

    public String get(String baseUri, String path, Map<String, String> params,
                      Map<String, String> headers) throws Exception {
        LOG.info("Executing GET request on " + baseUri);
        HttpGet httpGet = new HttpGet(getUri(baseUri, path, params));

        if (MapUtils.isNotEmpty(headers)) {
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }

        return execute(httpGet);
    }

    private String execute(HttpUriRequest httpReq) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        InputStream eis = null;
        try {
            CloseableHttpResponse response = httpClient.execute(httpReq);

            int statusCode = response.getStatusLine().getStatusCode();
            if (!(statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED)) {
                String reasonPhrase = response.getStatusLine().getReasonPhrase();
                String errResponse = IOUtils.toString(response.getEntity().getContent(), STR_UTF_8);
                throw new MarketoClientException(
                        String.format("Accessing %s failed. Status %d. Reason %s \n Error from server %s",
                                httpReq.getURI(), statusCode, reasonPhrase, errResponse));
            }

            HttpEntity responseEntity = response.getEntity();
            eis = responseEntity.getContent();
            return IOUtils.toString(eis, STR_UTF_8);
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                LOG.log(Level.FINE, "Error while closing HTTP Client", e);
            }

            try {
                if (eis != null) {
                    eis.close();
                }
            } catch (Exception e) {
                LOG.log(Level.FINE, "Error while closing InputStream", e);
            }
        }
    }

    private URI getUri(String baseUri, String path, Map<String, String> params) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(baseUri);
        builder.setPath(path);

        if (MapUtils.isNotEmpty(params)) {
            Set<Map.Entry<String, String>> entries = params.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                builder.addParameter(entry.getKey(), entry.getValue());
            }
        }

        return builder.build();
    }
}

