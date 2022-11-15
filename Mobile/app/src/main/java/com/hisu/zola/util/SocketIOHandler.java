package com.hisu.zola.util;

import com.hisu.zola.BuildConfig;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.util.local.LocalDataManager;

import java.net.URI;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketIOHandler {

    private static SocketIOHandler instance;
    private static Socket mSocketIO;

    private SocketIOHandler() {
        IO.Options mOptions = new IO.Options();
        User user = LocalDataManager.getCurrentUserInfo();
        if (user != null)
            mOptions.query = "_id=" + user.getId();
        mSocketIO = IO.socket(getConnectionURI(), mOptions);
    }

    public static synchronized SocketIOHandler getInstance() {
        if (instance == null)
            instance = new SocketIOHandler();
        return instance;
    }

    public Socket getSocketConnection() {
        return mSocketIO;
    }

    public void establishSocketConnection() {
        mSocketIO.connect();
    }

    public static void disconnect() {
        mSocketIO.disconnect();
    }

    public static void close() {
        mSocketIO.close();
    }

    private URI getConnectionURI() {
        return URI.create(BuildConfig.SERVER_URL);
    }
}