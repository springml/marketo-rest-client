package com.springml.marketo.rest.client.model.activities;

/**
 * Created by sam on 10/1/17.
 */
public class PagingToken {
    private String requestId;
    private Boolean success;
    private String nextPageToken;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }
}
