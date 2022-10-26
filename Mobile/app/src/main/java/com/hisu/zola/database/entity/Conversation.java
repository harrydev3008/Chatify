package com.hisu.zola.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

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

    @Ignore
    public Conversation() {
    }

    public Conversation(@NonNull String _id, List<User> member, User createdBy, String label) {
        this._id = _id;
        this.member = member;
        this.createdBy = createdBy;
        this.label = label;
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

    @Override
    public String toString() {
        return "Conversation{" +
                "_id='" + _id + '\'' +
                ", member=" + member +
                ", createdBy=" + createdBy +
                ", label='" + label + '\'' +
                '}';
    }
}