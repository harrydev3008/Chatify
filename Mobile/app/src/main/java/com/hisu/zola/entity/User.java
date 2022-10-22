package com.hisu.zola.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    @PrimaryKey
    @NonNull
    private String _id;
    private String username;
    private String phoneNumber;
    private String password;
    private String avatarURL;
    private boolean isVerifyOTP;

    private List<String> friends;

    @Ignore
    public User() {
    }

    public User(@NonNull String _id, String username, String phoneNumber, String password, String avatarURL, boolean isVerifyOTP) {
        this._id = _id;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.avatarURL = avatarURL;
        this.isVerifyOTP = isVerifyOTP;
    }

    @Ignore
    public User(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public boolean isVerifyOTP() {
        return isVerifyOTP;
    }

    public void setVerifyOTP(boolean verifyOTP) {
        isVerifyOTP = verifyOTP;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        return "User{" +
                "_id='" + _id + '\'' +
                ", username='" + username + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                ", isVerifyOTP=" + isVerifyOTP +
                ", friends=" + friends +
                '}';
    }
}