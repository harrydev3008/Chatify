package com.hisu.zola.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "conversations")
public class Conversation {

    @PrimaryKey
    @SerializedName("_id")
    @NonNull
    private String id;
    @TypeConverters(UserListConverter.class)
    private List<String> member;
    private String createdBy;
    private String label;

    @Ignore
    public Conversation() {
    }

    public Conversation(@NonNull String id, List<String> member, String createdBy, String label) {
        this.id = id;
        this.member = member;
        this.createdBy = createdBy;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getMember() {
        return member;
    }

    public void setMember(List<String> member) {
        this.member = member;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}