package com.vodafone.frt.models;

/**
 * Created by vishal on 13/12/17
 */
public class PTRResponseSaveRouteAction {
    private String status, error_message, results;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }
}
