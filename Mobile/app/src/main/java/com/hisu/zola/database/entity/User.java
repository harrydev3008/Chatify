package com.hisu.zola.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.hisu.zola.database.type_converter.ListFriendConverter;
import com.hisu.zola.database.type_converter.ListUserConverter;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "users")
public class User implements Serializable {

    @PrimaryKey
    @NonNull
    private String _id;
    private String username;
    private String phoneNumber;
    private String password;
    private String avatarURL;
    private boolean isVerifyOTP;
    private String dob;
    private boolean gender;//T -> male, false -> female

    @TypeConverters(ListFriendConverter.class)
    private List<User> friends;

    @TypeConverters(ListFriendConverter.class)
    private List<User> friendsQueue;

    @Ignore
    public User() {
    }

    public User(@NonNull String _id, String username, String phoneNumber, String password, String avatarURL, boolean isVerifyOTP, String dob, boolean gender) {
        this._id = _id;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.avatarURL = avatarURL;
        this.isVerifyOTP = isVerifyOTP;
        this.dob = dob;
        this.gender = gender;
    }

    @Ignore
    public User(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getId() {
        return _id;
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

    public List<User> getFriendsQueue() {
        return friendsQueue;
    }

    public void setFriendsQueue(List<User> friendsQueue) {
        this.friendsQueue = friendsQueue;
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
                ", dob='" + dob + '\'' +
                ", gender=" + gender +
                ", friends=" + friends +
                '}';
    }
}