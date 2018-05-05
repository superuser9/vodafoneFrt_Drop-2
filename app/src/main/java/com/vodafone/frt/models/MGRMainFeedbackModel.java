package com.vodafone.frt.models;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ajay Tiwari on 3/15/2018.
 */

public class MGRMainFeedbackModel {
    private String status,error_message;

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

    public List<MGRResponseFeedback> getResults() {
        if (results == null)
            return results = new ArrayList<>();
        return results;
    }

    public void setResults(List<MGRResponseFeedback> results) {
        this.results = results;
    }

    private List<MGRResponseFeedback> results;


}
