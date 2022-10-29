package com.hisu.zola.listeners;

import com.hisu.zola.database.entity.Message;

public interface IOnItemTouchListener {
    void longPress(Message message);
}