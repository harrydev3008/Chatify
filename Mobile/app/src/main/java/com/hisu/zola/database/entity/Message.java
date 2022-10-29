package com.hisu.zola.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.hisu.zola.database.type_converter.ListMediaConverter;
import com.hisu.zola.database.type_converter.UserConverter;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "messages")
public class Message implements Serializable {
    @PrimaryKey
    @NonNull
    private String _id;
    private String conversation;
    @TypeConverters(UserConverter.class)
    private User sender;
    private String text;
    private String type;
    private String createdAt;
    private String updatedAt;
    @TypeConverters(ListMediaConverter.class)
    private List<Media> media;
    private boolean isDelete;

    @Ignore
    public Message() {
    }

    public Message(@NonNull String _id, String conversation, User sender, String text, String type, String createdAt, String updatedAt, List<Media> media, boolean isDelete) {
        this._id = _id;
        this.conversation = conversation;
        this.sender = sender;
        this.text = text;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.media = media;
        this.isDelete = isDelete;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Media> getMedia() {
        return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    @Override
    public String toString() {
        return "Message{" +
                "_id='" + _id + '\'' +
                ", conversation='" + conversation + '\'' +
                ", sender=" + sender +
                ", text='" + text + '\'' +
                ", type='" + type + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", media=" + media +
                ", isDelete=" + isDelete +
                '}';
    }
}