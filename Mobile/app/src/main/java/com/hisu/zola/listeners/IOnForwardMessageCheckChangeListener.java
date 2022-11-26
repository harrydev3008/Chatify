package com.hisu.zola.listeners;

import com.hisu.zola.database.entity.Conversation;

public interface IOnForwardMessageCheckChangeListener {
    void itemCheck(Conversation conversation, boolean isCheck);
}
