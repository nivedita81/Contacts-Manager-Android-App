package com.example.group8_inclass12;

public class Credentials {
    String userID;

    public Credentials(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "userID='" + userID + '\'' +
                '}';
    }
}
