package com.app.owl;

/**
 * Created by Christiane on 1/5/18.
 */

public class MainUser {


    String userId;
    String userUid;
    String userName;
    String userEmail;

    public MainUser(){

    }

    public MainUser(String userId, String userUid, String userName, String userEmail) {
        this.userId = userId;
        this.userUid = userUid;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserUid() {
        return userUid;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }


}
