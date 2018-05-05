package com.vodafone.frt.models;

public class SendMsgResp extends BaseModel {
    private BaseMessageResp results;

    public BaseMessageResp getResults() {
        return results;
    }

    public void setResults(BaseMessageResp results) {
        this.results = results;
    }
}
