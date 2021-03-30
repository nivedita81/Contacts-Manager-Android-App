package com.example.group8_inclass12;

import java.io.Serializable;

public class User implements Serializable {
    String name, email;
    long phone;
    String picture;
    String docId;


    public User(String name, String email, long phone, String picture, String docId) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.picture = picture;
        this.docId = docId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone=" + phone +
                ", picture='" + picture + '\'' +
                ", docId='" + docId + '\'' +
                '}';
    }
}
