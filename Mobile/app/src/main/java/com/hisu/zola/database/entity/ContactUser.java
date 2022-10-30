package com.hisu.zola.database.entity;

import android.graphics.Bitmap;
import android.net.Uri;

public class ContactUser {
    private String name;
    private String phoneNumber;
    private String avatar;
    private Bitmap imageBitmap;
    private Uri imageUri;

    public ContactUser() {
    }

    public ContactUser(String name, String phoneNumber, Uri imageUri) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.imageUri = imageUri;
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

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}