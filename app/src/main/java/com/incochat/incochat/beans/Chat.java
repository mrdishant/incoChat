package com.incochat.incochat.beans;

/**
 * Created by mrdis on 4/7/2018.
 */

public class Chat {

    String sender,lastTxt;
    boolean anonymous;


    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getLastTxt() {
        return lastTxt;
    }

    public void setLastTxt(String lastTxt) {
        this.lastTxt = lastTxt;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }
}
