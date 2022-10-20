package com.hisu.zola.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "users")
public class User {

    @PrimaryKey
    @SerializedName("_id")
    @NonNull
    private String id;
    private String username;
    private String phoneNumber;
    private String password;
    private String avatarURL;
    private boolean isVerifyOTP;

    @Ignore
    private List<User> friends;

    @Ignore
    public User() {
    }

    public User(@NonNull String id, String username, String phoneNumber, String password, String avatarURL, boolean isVerifyOTP) {
        this.id = id;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.avatarURL = avatarURL;
        this.isVerifyOTP = isVerifyOTP;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }
}