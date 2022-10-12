package com.hisu.zola.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
@Entity(tableName = "conversations")
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
}