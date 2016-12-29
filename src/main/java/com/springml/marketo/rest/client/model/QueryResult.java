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
