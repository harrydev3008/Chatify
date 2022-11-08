package com.hisu.zola.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "contact_users")
public class ContactUser implements Serializable {

    @PrimaryKey
    @NonNull
    private String _id;
    private String username;
    private String phoneNumber;
    private String avatarURL;
    private String dob;
    private boolean gender;//T -> male, false -> female
    private boolean isFriend;

    @Ignore
    public ContactUser() {
    }

    public ContactUser(@NonNull String _id, String username, String phoneNumber, String avatarURL, String dob, boolean gender, boolean isFriend) {
        this._id = _id;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.avatarURL = avatarURL;
        this.dob = dob;
        this.gender = gender;
        this.isFriend = isFriend;
    }

    @Ignore
    public ContactUser(@NonNull String _id, String username, String phoneNumber) {
        this._id = _id;
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }

    @NonNull
    public String get_id() {
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

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
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

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    @Override
    public String toString() {
        return "ContactUser{" +
                "_id='" + _id + '\'' +
                ", username='" + username + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                ", dob='" + dob + '\'' +
                ", gender=" + gender +
                ", isFriend=" + isFriend +
                '}';
    }
}