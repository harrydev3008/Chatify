package com.hisu.zola.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
public class ConversationHolder {
//  Todo: this is for test, will change later
    private String id;
    private boolean isGroup;
    private int coverPhoto;
    private String name;
    private String lastMessage;
    private int unreadMessages;
}