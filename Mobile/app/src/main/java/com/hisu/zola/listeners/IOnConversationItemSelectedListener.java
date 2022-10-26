package com.hisu.zola.listeners;

import com.hisu.zola.database.entity.Conversation;

public interface IOnConversationItemSelectedListener {
    void openConversation(Conversation conversation, String conversationName);
}