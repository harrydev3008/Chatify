package com.hisu.zola.fragments.conversation;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.ConversationAdapter;
import com.hisu.zola.database.Database;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.Media;
import com.hisu.zola.database.entity.Message;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.database.repository.MessageRepository;
import com.hisu.zola.databinding.FragmentConversationListBinding;
import com.hisu.zola.databinding.LayoutPopupBinding;
import com.hisu.zola.fragments.AddFriendFragment;
import com.hisu.zola.util.ApiService;
import com.hisu.zola.util.EditTextUtil;
import com.hisu.zola.util.NetworkUtil;
import com.hisu.zola.util.SocketIOHandler;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.view_model.ConversationListViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationListFragment extends Fragment {

    private FragmentConversationListBinding mBinding;
    private MainActivity mMainActivity;
    private ConversationListViewModel viewModel;
    private ConversationAdapter adapter;
    private PopupWindow popupMenu;
    private Socket mSocket;
    private MessageRepository messageRepository;
    private List<Conversation> conversationList;
    private List<Conversation> filteredList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentConversationListBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        conversationList = new ArrayList<>();
        filteredList = new ArrayList<>();
        mMainActivity.setProgressbarVisibility(View.GONE);
        messageRepository = new MessageRepository(mMainActivity.getApplication());

        SocketIOHandler.getInstance();
        SocketIOHandler.getInstance().establishSocketConnection();

        mSocket = SocketIOHandler.getInstance().getSocketConnection();
        mSocket.on("addMemberToGroup-receive", onReceive);
        mSocket.on("deleteMemberGroup-receive", onReceiveRemoveMember);
        mSocket.on("changeGroupName-receive", onReceiveChangeGroupName);
        mSocket.on("outGroup-receive", onReceiveOutGroup);
        mSocket.on("addConversation-receive", onReceiveNewGroup);
        mSocket.on("changeCreatorGroup-receive", onReceiveNewAdmin);
        mSocket.on("deleteGroup-receive", onDisbandGroup);
        mSocket.on("msg-receive", onMessageReceive);
        mSocket.on("delete-receive", onMessageDeleteReceive);
        initConversationListRecyclerView();
        filter();
        initPopupMenu();

        tapToCloseApp();
        EditTextUtil.toggleShowClearIconOnEditText(mMainActivity, mBinding.edtSearch);
        EditTextUtil.clearTextOnSearchEditText(mBinding.edtSearch);
        addMoreFriendEvent();

        if (NetworkUtil.isConnectionAvailable(mMainActivity))
            loadConversationList();
    }

    private void initConversationListRecyclerView() {
        adapter = new ConversationAdapter(mMainActivity);

        viewModel = new ViewModelProvider(mMainActivity).get(ConversationListViewModel.class);
        viewModel.getData().observe(mMainActivity, new Observer<List<Conversation>>() {
            @Override
            public void onChanged(List<Conversation> conversations) {

                if (conversations == null) return;
                conversationList.clear();
                conversationList.addAll(conversations);

                List<Conversation> curConversations = new ArrayList<>();
                conversations.forEach(conversation -> {
                    conversation.getMember().forEach(member -> {
                        if (member.getId().equalsIgnoreCase(LocalDataManager.getCurrentUserInfo().getId()))
                            curConversations.add(conversation);
                    });
                });

                adapter.setConversations(curConversations);
                mBinding.rvConversationList.setAdapter(adapter);
            }
        });

        adapter.setOnConversationItemSelectedListener((conversation, conversationName) -> {
            mMainActivity.setBottomNavVisibility(View.GONE);
            mMainActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left, R.anim.slide_out_left,
                            R.anim.slide_out_right, R.anim.slide_out_right)
                    .replace(
                            mMainActivity.getViewContainerID(),
                            ConversationFragment.newInstance(conversation, conversationName)
                    )
                    .addToBackStack("Single_Conversation")
                    .commit();
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                mMainActivity, RecyclerView.VERTICAL, false
        );

        mBinding.rvConversationList.setLayoutManager(linearLayoutManager);
    }

    private void filter() {
        mBinding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filteredList.clear();
                String query = editable.toString().trim();
                if (query.isEmpty())
                    adapter.setConversations(conversationList);
                else {
                    for (Conversation conversation : conversationList) {
                        if (conversation.getLabel() == null) {
                            User other = getConversation(conversation.getMember());
                            if (other.getUsername().toLowerCase().contains(query))
                                filteredList.add(conversation);
                        } else if (conversation.getLabel().toLowerCase().contains(query))
                            filteredList.add(conversation);
                    }
                    adapter.setConversations(filteredList);
                }
            }
        });
    }

    private User getConversation(List<User> members) {
        User currentUser = LocalDataManager.getCurrentUserInfo();
        for (User member : members) {
            if (!member.getId().equalsIgnoreCase(currentUser.getId()))
                return member;
        }
        return currentUser;
    }

    private void loadConversationList() {
        ApiService.apiService.getConversations().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Conversation>> call, @NonNull Response<List<Conversation>> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    List<Conversation> conversations = response.body();
                    if (conversations != null && conversations.size() != 0) {
                        viewModel.insertAll(conversations);
                        loadMessageList(conversations);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Conversation>> call, @NonNull Throwable t) {
                Log.e("API_ERR", t.getLocalizedMessage());
            }
        });
    }

    private void loadMessageList(List<Conversation> conversations) {
        Database.dbExecutor.execute(() -> {
            conversations.forEach(conversation -> {
                loadConversationMessage(conversation.getId());
            });
        });
    }

    private void loadConversationMessage(String conversationID) {
        JsonObject object = new JsonObject();
        object.addProperty("conversation", conversationID);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), object.toString());

        ApiService.apiService.getConversationMessages(body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                Gson gson = new Gson();

                JsonElement json = gson.toJsonTree(response.body());

                JsonObject obj = gson.fromJson(json, JsonObject.class);
                JsonArray array = obj.getAsJsonArray("data");

                List<Message> messages = gson.fromJson(array, new TypeToken<List<Message>>() {
                }.getType());

                messageRepository.insertAll(messages);
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                Log.e(ConversationFragment.class.getName(), t.getLocalizedMessage());
            }
        });
    }

    private void tapToCloseApp() {
        mBinding.mBtnCloseSearch.setOnClickListener(view -> {
            mMainActivity.onBackPressed();
        });
    }

    private void addMoreFriendEvent() {
        mBinding.mBtnAddFriend.setOnClickListener(view -> {
            popupMenu.showAsDropDown(view, 0, 0);
            View container = (View) popupMenu.getContentView().getParent();
            WindowManager wm = (WindowManager) mMainActivity.getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
            p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            p.dimAmount = 0.3f;
            wm.updateViewLayout(container, p);
        });
    }

    private void initPopupMenu() {
        LayoutInflater inflater = (LayoutInflater)
                mMainActivity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LayoutPopupBinding popupBinding = LayoutPopupBinding.inflate(inflater, null, false);

        popupMenu = new PopupWindow(popupBinding.getRoot(), 520, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        popupBinding.tvAddGroup.setOnClickListener(view -> {
            popupMenu.dismiss();
            mMainActivity.addFragmentToBackStack(new AddNewGroupFragment());
        });

        popupBinding.tvAddFriend.setOnClickListener(view -> {
            popupMenu.dismiss();
            mMainActivity.addFragmentToBackStack(new AddFriendFragment());
        });
    }

    private final Emitter.Listener onReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mMainActivity.runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                if (data != null) {

                    try {
                        Gson gson = new Gson();

                        Conversation conversation = gson.fromJson(data.toString(), Conversation.class);
                        viewModel.insertOrUpdate(conversation);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private final Emitter.Listener onReceiveRemoveMember = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mMainActivity.runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                if (data != null) {

                    try {
                        Gson gson = new Gson();

                        Conversation conversation = gson.fromJson(data.toString(), Conversation.class);
                        viewModel.insertOrUpdate(conversation);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private final Emitter.Listener onReceiveChangeGroupName = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mMainActivity.runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                if (data != null) {

                    try {
                        Gson gson = new Gson();

                        Conversation conversation = gson.fromJson(data.toString(), Conversation.class);
                        viewModel.insertOrUpdate(conversation);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private final Emitter.Listener onReceiveOutGroup = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mMainActivity.runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                if (data != null) {

                    try {
                        Gson gson = new Gson();

                        Conversation conversation = gson.fromJson(data.toString(), Conversation.class);
                        viewModel.insertOrUpdate(conversation);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private final Emitter.Listener onReceiveNewGroup = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mMainActivity.runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                if (data != null) {

                    try {
                        Gson gson = new Gson();

                        Conversation conversation = gson.fromJson(data.toString(), Conversation.class);
                        viewModel.insertOrUpdate(conversation);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private final Emitter.Listener onReceiveNewAdmin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mMainActivity.runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                if (data != null) {

                    try {
                        Gson gson = new Gson();

                        Conversation conversation = gson.fromJson(data.toString(), Conversation.class);
                        viewModel.insertOrUpdate(conversation);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private final Emitter.Listener onDisbandGroup = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mMainActivity.runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                if (data != null) {

                    try {
                        Gson gson = new Gson();

                        Conversation conversation = gson.fromJson(data.toString(), Conversation.class);
                        viewModel.delete(conversation);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private final Emitter.Listener onMessageReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mMainActivity.runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                if (data != null) {

                    try {

                        Gson gson = new Gson();

                        Conversation conversation = gson.fromJson(data.getString("conversation"), Conversation.class);

                        User sender = gson.fromJson(data.getString("sender"), User.class);

                        List<Media> media = gson.fromJson(data.get("media").toString(), new TypeToken<List<Media>>() {
                        }.getType());

                        Message message = new Message(data.getString("_id"), conversation.getId(), sender, data.getString("text"),
                                data.getString("type"), data.getString("createdAt"), data.getString("updatedAt"), media, false);

                        conversation.setLastMessage(message);
                        viewModel.insertOrUpdate(conversation);
                        messageRepository.insertOrUpdate(message);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private final Emitter.Listener onMessageDeleteReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mMainActivity.runOnUiThread(() -> {
                JSONObject data = (JSONObject) args[0];
                if (data != null) {
                    try {
                        Gson gson = new Gson();

                        Conversation conversation = gson.fromJson(data.getString("conversation"), Conversation.class);

                        User sender = gson.fromJson(data.getString("sender"), User.class);
                        Log.e("sender", sender.toString());

                        List<Media> media = gson.fromJson(data.get("media").toString(), new TypeToken<List<Media>>() {
                        }.getType());

                        Message message = new Message(data.getString("_id"), conversation.getId(), sender, data.getString("text"),
                                data.getString("type"), data.getString("createdAt"), data.getString("updatedAt"), media, true);

                        conversation.setLastMessage(message);
                        viewModel.insertOrUpdate(conversation);
                        messageRepository.insertOrUpdate(message);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}