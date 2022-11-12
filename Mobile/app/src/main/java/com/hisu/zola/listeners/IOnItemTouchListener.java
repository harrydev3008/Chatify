package com.hisu.zola.listeners;

import android.view.View;

import com.hisu.zola.database.entity.Message;

public interface IOnItemTouchListener {
    void longPress(Message message, View view);
}