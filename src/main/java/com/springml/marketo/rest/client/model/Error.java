package com.springml.marketo.rest.client.model;

/**
 * Created by sam on 29/12/16.
 */
public class Error {
    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
