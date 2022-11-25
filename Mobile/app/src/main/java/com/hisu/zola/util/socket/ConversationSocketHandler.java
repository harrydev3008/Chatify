package com.hisu.zola.util.socket;

import android.app.Application;

import com.google.gson.Gson;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.ConversationRepository;
import com.hisu.zola.database.repository.MessageRepository;
import com.hisu.zola.util.local.LocalDataManager;

import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class ConversationSocketHandler {

    private static ConversationSocketHandler INSTANCE;

    private ConversationRepository conversationRepository;
    private MessageRepository messageRepository;

    private ConversationSocketHandler() {
    }

    private ConversationSocketHandler(Application application) {
        conversationRepository = new ConversationRepository(application);
        messageRepository = new MessageRepository(application);
    }

    public static synchronized ConversationSocketHandler getINSTANCE(Application application) {
        if (INSTANCE == null) {

            INSTANCE = new ConversationSocketHandler(application);
        }
        return INSTANCE;
    }

    public final Emitter.Listener onAddMemberToGroupReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            if (data != null) {

                try {
                    Gson gson = new Gson();

                    Conversation conversation = gson.fromJson(data.toString(), Conversation.class);
                    conversationRepository.insertOrUpdate(conversation);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public final Emitter.Listener onReceiveRemoveMember = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            if (data != null) {
                try {
                    boolean isKicked = true;
                    Gson gson = new Gson();

                    Conversation conversation = gson.fromJson(data.toString(), Conversation.class);

                    for (User member : conversation.getMember()) {
                        if (member.getId().equalsIgnoreCase(LocalDataManager.getCurrentUserInfo().getId())) {
                            isKicked = false;
                            break;
                        }
                    }

                    if (isKicked) {
                        conversationRepository.setDisbandGroup(conversation, "kick");
                        messageRepository.deleteAllMessage(conversation.getId());
                    } else {
                        conversationRepository.insertOrUpdate(conversation);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public final Emitter.Listener onReceiveOutGroup = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            if (data != null) {
                try {
                    Gson gson = new Gson();

                    Conversation conversation = gson.fromJson(data.toString(), Conversation.class);
                    conversationRepository.insertOrUpdate(conversation);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public final Emitter.Listener onReceiveChangeGroupName = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            if (data != null) {
                try {
                    Gson gson = new Gson();

                    Conversation conversation = gson.fromJson(data.toString(), Conversation.class);
                    conversationRepository.insertOrUpdate(conversation);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public final Emitter.Listener onReceiveNewGroup = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            if (data != null) {
                try {
                    Gson gson = new Gson();

                    Conversation conversation = gson.fromJson(data.toString(), Conversation.class);
                    conversationRepository.insertOrUpdate(conversation);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public final Emitter.Listener onReceiveNewAdmin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            if (data != null) {

                try {
                    Gson gson = new Gson();

                    Conversation conversation = gson.fromJson(data.toString(), Conversation.class);
                    conversationRepository.insertOrUpdate(conversation);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public final Emitter.Listener onDisbandGroup = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            if (data != null) {

                try {

                    Gson gson = new Gson();

                    Conversation conversation = gson.fromJson(data.toString(), Conversation.class);

                    conversationRepository.setDisbandGroup(conversation, "disband");
                    messageRepository.deleteAllMessage(conversation.getId());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
}