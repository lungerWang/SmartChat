package com.lunger.smartchat.model;

import java.io.Serializable;

/**
 * Created by Lunger on 2016/11/30.
 */
public class ChatModel implements Serializable{
    private String messageSend;
    private String messageReceived;
    private boolean isRobotMsg;

    public ChatModel(String messageSend, String messageReceived, boolean isRobotMsg) {
        this.messageSend = messageSend;
        this.messageReceived = messageReceived;
        this.isRobotMsg = isRobotMsg;
    }

    public String getMessageSend() {
        return messageSend;
    }

    public void setMessageSend(String messageSend) {
        this.messageSend = messageSend;
    }

    public String getMessageReceived() {
        return messageReceived;
    }

    public void setMessageReceived(String messageReceived) {
        this.messageReceived = messageReceived;
    }

    public boolean isRobotMsg() {
        return isRobotMsg;
    }

    public void setIsRobotMsg(boolean isRobotMsg) {
        this.isRobotMsg = isRobotMsg;
    }
}
