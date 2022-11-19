package com.hisu.zola.database.repository;

import android.app.Application;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import androidx.lifecycle.LiveData;

import com.hisu.zola.database.Database;
import com.hisu.zola.database.dao.ConversationDAO;
import com.hisu.zola.database.dao.MessageDAO;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.Media;
import com.hisu.zola.database.entity.Message;

import java.io.File;
import java.util.List;

public class MessageRepository {
    private final MessageDAO messageDAO;
    private final ConversationDAO conversationDAO;
    private final Context context;

    public MessageRepository(Application application) {
        this.context = application;
        Database database = Database.getDatabase(application);
        messageDAO = database.messageDAO();
        conversationDAO = database.conversationDAO();
    }

    public LiveData<List<Message>> getData(String conversation) {
        return messageDAO.getMessages(conversation);
    }

    public LiveData<List<Message>> getImageMessage(String conversation, String type) {
        return messageDAO.getMessagesImage(conversation, type);
    }

    public void insertOrUpdate(Message message) {
        Database.dbExecutor.execute(() -> {
            if (messageDAO.getMessageById(message.getId()) == null)
                insert(message);
            else
                update(message);
        });
    }

    public void insertAll(List<Message> messages) {
        Database.dbExecutor.execute(() -> {
            messages.forEach(message -> {
                if (messageDAO.getMessageById(message.getId()) == null) {
                    messageDAO.insert(message);
                } else {
                    messageDAO.unsent(message.getId(), message.getDeleted());
                }
            });
        });
    }

    private void insert(Message message) {
        Database.dbExecutor.execute(() -> {
            messageDAO.insert(message);
        });
    }

    private void update(Message message) {
        Database.dbExecutor.execute(() -> {
            messageDAO.updateMessage(message);
        });
    }

    public void unsent(Message message) {
        Database.dbExecutor.execute(() -> {
            messageDAO.unsent(message.getId(), message.getDeleted());
        });
    }

    public void deleteAllMessage(String conversationID) {
        Database.dbExecutor.execute(() -> {
            messageDAO.removeAllMessageOfConversation(conversationID);
        });
    }

    public LiveData<Conversation> getConversationInfo(String id) {
        return conversationDAO.getConversationInfoById(id);
    }

    private void saveFile(Message message, String url) {

        DownloadManager.Request dmr = new DownloadManager.Request(Uri.parse(url));

        String fileName = message.getText();

        dmr.setTitle(fileName);
        dmr.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM, fileName);
        dmr.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        dmr.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long enqueue = manager.enqueue(dmr);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                if (enqueue == reference) {
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), fileName);
                    if (file.exists()) {
                        String path = file.getAbsolutePath();
                        List<Media> media = message.getMedia();
                        Media mediaItem = media.get(0);
                        mediaItem.setUrl(path);

                        message.setMedia(List.of(mediaItem));
                        Database.dbExecutor.execute(() -> {
                            messageDAO.updateMessage(message);
                        });
                    }
                }
            }
        };

        context.registerReceiver(receiver, filter);
    }
}