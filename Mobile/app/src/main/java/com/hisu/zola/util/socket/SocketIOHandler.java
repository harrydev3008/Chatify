package com.hisu.zola.util.socket;

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
        if (user != null && user.getId() != null && user.getFriends() != null) {
            StringBuilder friends = new StringBuilder();
            for (User friend : user.getFriends())
                friends.append(",").append(friend.getId());

            if (friends.length() > 0)
                friends = friends.deleteCharAt(0);

            mOptions.query = "_id=" + user.getId() + "&friends=" + friends;
        }
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
        mSocketIO.off();
        mSocketIO.close();
    }

    private URI getConnectionURI() {
        return URI.create(BuildConfig.SERVER_URL);
    }

    public static void reconnect() {
        instance = new SocketIOHandler();
    }
}