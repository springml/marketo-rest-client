package com.springml.marketo.rest.client.model;

import java.util.List;
import java.util.Map;

/**
 * Model class for LeadDatabase Query Result
 */
public class QueryResult {
    private String requestId;
    private List<Map<String, String>> result;
    private List<Error> errors;
    private boolean success;
    private String nextPageToken;
    private String object;
    private String filterType;
    private String filterValues;
    private List<String> fields;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Map<String, String>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, String>> result) {
        this.result = result;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(String filterValues) {
        this.filterValues = filterValues;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "QueryResult{" +
                "requestId='" + requestId + '\'' +
                ", result=" + result +
                ", errors=" + errors +
                ", success=" + success +
                '}';
    }
}
