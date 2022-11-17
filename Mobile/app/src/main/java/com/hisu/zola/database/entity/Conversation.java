package com.hisu.zola.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.hisu.zola.database.type_converter.ListUserConverter;
import com.hisu.zola.database.type_converter.MessageConverter;
import com.hisu.zola.database.type_converter.UserConverter;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "conversations")
public class Conversation implements Serializable {

    @PrimaryKey
    @NonNull
    private String _id;
    @TypeConverters(ListUserConverter.class)
    private List<User> member;
    @TypeConverters(UserConverter.class)
    private User createdBy;
    private String label;
    @TypeConverters(MessageConverter.class)
    private Message lastMessage;

    @ColumnInfo(name = "disband", defaultValue = "false")
    private String disband;
    @SerializedName("isGroup")
    private Boolean isGroup;

    private String updatedAt;

    @Ignore
    public Conversation() {
    }

    public Conversation(@NonNull String _id, List<User> member, User createdBy, String label, String updatedAt, String disband, boolean isGroup) {
        this._id = _id;
        this.member = member;
        this.createdBy = createdBy;
        this.label = label;
        this.updatedAt = updatedAt;
        this.disband = disband;
        this.isGroup = isGroup;
    }

    public Boolean getGroup() {
        return isGroup;
    }

    public void setGroup(Boolean group) {
        isGroup = group;
    }

    public String getDisband() {
        return disband;
    }

    public void setDisband(String disband) {
        this.disband = disband;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public List<User> getMember() {
        return member;
    }

    public void setMember(List<User> member) {
        this.member = member;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "_id='" + _id + '\'' +
                ", member=" + member +
                ", createdBy=" + createdBy +
                ", label='" + label + '\'' +
                ", lastMessage=" + lastMessage +
                ", disband='" + disband + '\'' +
                ", isGroup=" + isGroup +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}