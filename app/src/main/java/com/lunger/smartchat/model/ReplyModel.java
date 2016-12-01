package com.lunger.smartchat.model;

import java.io.Serializable;

/**
 * Created by Lunger on 2016/11/30.
 */
public class ReplyModel implements Serializable{
    private long code;
    private String text;

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
