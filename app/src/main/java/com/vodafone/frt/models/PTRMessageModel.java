package com.vodafone.frt.models;

import android.support.annotation.NonNull;

/**
 * Created by Ashutosh Kumar on 30-Jan-18.
 */

public class PTRMessageModel implements Comparable<PTRMessageModel> {

    /** Note
     messageType -Sent ,Recieved
     messageCongtentType -Text ,Binary
     **/
    private int messageTo, messageFrom, messageId;
    private String messageText, messageType,messageTime;

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public int getMessageTo() {
        return messageTo;
    }

    public void setMessageTo(int messageTo) {
        this.messageTo = messageTo;
    }

    public int getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(int messageFrom) {
        this.messageFrom = messageFrom;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }


    @Override
    public int compareTo(@NonNull PTRMessageModel o) {
        return getMessageTime().compareTo(o.getMessageTime());
    }
}
