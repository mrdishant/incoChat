package com.incochat.incochat.beans;

import java.util.Date;

/**
 * Created by mrdis on 1/31/2018.
 */

public class Message {

    String text,url;
    String time;
    boolean usersent;
    String id;
    String bucketId;
    boolean adult;

    public String getId() {
        return id;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isUsersent() {
        return usersent;
    }

    public void setUsersent(boolean usersent) {
        this.usersent = usersent;
    }

}
