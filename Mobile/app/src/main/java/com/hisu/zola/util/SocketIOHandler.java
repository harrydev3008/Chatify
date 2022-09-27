package com.hisu.zola.util;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.hisu.zola.BuildConfig;

import java.net.URI;

public class SocketIOHandler {

    private static SocketIOHandler instance;
    private static Socket mSocketIO;

    private SocketIOHandler() {
        mSocketIO = IO.socket(getConnectionURI());
    }

    public static synchronized SocketIOHandler getInstance() {
        if (instance == null)
            instance = new SocketIOHandler();
        return instance;
    }

    public static Socket getSocketConnection() {
        return mSocketIO;
    }

    public static void establishSocketConnection() {
        mSocketIO.connect();
    }

    public static void closeSocketConnection() {
        mSocketIO.disconnect();
    }

    private URI getConnectionURI() {
        return URI.create(BuildConfig.SERVER_URL + ":" + BuildConfig.SERVER_PORT);
    }
}