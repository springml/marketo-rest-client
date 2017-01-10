
package com.springml.marketo.rest.client.model.activities;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.springml.marketo.rest.client.model.Error;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityTypes {

    @JsonProperty("requestId")
    private String requestId;
    @JsonProperty("result")
    private List<Result> result = null;
    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("errors")
    private List<Error> errors;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "ActivityTypes{" +
                "requestId='" + requestId + '\'' +
                ", result=" + result +
                ", success=" + success +
                ", errors=" + errors +
                '}';
    }
}
