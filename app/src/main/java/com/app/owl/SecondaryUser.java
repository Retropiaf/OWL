package com.app.owl;

/**
 * Created by Christiane on 1/5/18.
 */

public class SecondaryUser {

    String userId;
    String userName;

    public SecondaryUser(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
}
