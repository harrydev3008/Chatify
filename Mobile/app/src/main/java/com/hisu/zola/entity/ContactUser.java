package com.hisu.zola.entity;

import android.graphics.Bitmap;

public class ContactUser {
    private String name;
    private String phoneNumber;
    private String avatar;
    private Bitmap imageBitmap;

    public ContactUser() {
    }

    public ContactUser(String name, String phoneNumber, String avatar) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}