package com.incochat.incochat.beans;

import java.util.ArrayList;

/**
 * Created by mrdis on 1/24/2018.
 */

public class User {

    String name,profileurl,phonenumber,token,txt;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileurl() {
        return profileurl;
    }

    public void setProfileurl(String profileurl) {
        this.profileurl = profileurl;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTxt() {
        return txt;
    }


    public void setTxt(String txt) {
        this.txt = txt;
    }
}
