package com.vodafone.frt.models;

import java.util.List;

public class ChatMessagesResp extends BaseModel {
    private List<BaseMessageResp> results;

    public List<BaseMessageResp> getResults() {
        return results;
    }

    public void setResults(List<BaseMessageResp> results) {
        this.results = results;
    }
}
