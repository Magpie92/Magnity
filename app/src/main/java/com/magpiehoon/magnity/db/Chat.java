package com.magpiehoon.magnity.db;

/**
 * Created by magpiehoon on 2017. 5. 29..
 */

public class Chat {
    private String mName;
    private String mMessage;
    private String mUid;

    public Chat() {
    }

    public Chat(String name, String message, String uid) {
        mName = name;
        mMessage = message;
        mUid = uid;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }
}
