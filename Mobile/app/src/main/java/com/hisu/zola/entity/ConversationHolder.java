package com.hisu.zola.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "conversation_holder")
public class ConversationHolder {
//  Todo: this is for test, will change later
    @PrimaryKey
    @NonNull
    private String id;
    private boolean isGroup;
    private int coverPhoto;
    private String name;
    private String lastMessage;
    private int unreadMessages;

    @Ignore
    public ConversationHolder() {
    }

    public ConversationHolder(@NonNull String id, boolean isGroup, int coverPhoto, String name, String lastMessage, int unreadMessages) {
        this.id = id;
        this.isGroup = isGroup;
        this.coverPhoto = coverPhoto;
        this.name = name;
        this.lastMessage = lastMessage;
        this.unreadMessages = unreadMessages;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public int getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(int coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }
}