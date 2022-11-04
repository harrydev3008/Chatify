package com.hisu.zola.fragments.conversation;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.ConversationAdapter;
import com.hisu.zola.databinding.FragmentConversationListBinding;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.fragments.AddFriendFragment;
import com.hisu.zola.util.ApiService;
import com.hisu.zola.util.EditTextUtil;
import com.hisu.zola.util.SocketIOHandler;
import com.hisu.zola.util.local.LocalDataManager;
import com.hisu.zola.view_model.ConversationListViewModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationListFragment extends Fragment {

    private FragmentConversationListBinding mBinding;
    private MainActivity mMainActivity;
    private ConversationListViewModel viewModel;
    private ConversationAdapter adapter;
    private PopupMenu popupMenu;
    private Socket mSocket;

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
        mMainActivity.setProgressbarVisibility(View.GONE);

        SocketIOHandler.getInstance().establishSocketConnection();
        mSocket = SocketIOHandler.getInstance().getSocketConnection();
        mSocket.on("addMemberToGroup-receive", onReceive);
        mSocket.on("deleteMemberGroup-receive", onReceiveRemoveMember);
        mSocket.on("changeGroupName-receive", onReceiveChangeGroupName);
        mSocket.on("outGroup-receive", onReceiveOutGroup);
        mSocket.on("addConversation-receive", onReceiveNewGroup);

        initConversationListRecyclerView();

        initPopupMenu();

        tapToCloseApp();
        EditTextUtil.toggleShowClearIconOnEditText(mMainActivity, mBinding.edtSearch);
        EditTextUtil.clearTextOnSearchEditText(mBinding.edtSearch);
        addMoreFriendEvent();

        loadConversationList();
    }

    private void initConversationListRecyclerView() {
        adapter = new ConversationAdapter(mMainActivity);

        viewModel = new ViewModelProvider(mMainActivity).get(ConversationListViewModel.class);
        viewModel.getData().observe(mMainActivity, new Observer<List<Conversation>>() {
            @Override
            public void onChanged(List<Conversation> conversations) {

                if(conversations == null) return;

                List<Conversation> curConversations = new ArrayList<>();
                conversations.forEach(conversation -> {
                    conversation.getMember().forEach(member -> {
                        if (member.getId().equalsIgnoreCase(LocalDataManager.getCurrentUserInfo().getId()))
                            curConversations.add(conversation);
                    });
                });

                curConversations.sort((c1, c2) -> c2.getUpdatedAt()
                        .compareToIgnoreCase(c1.getUpdatedAt()));

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

    private void loadConversationList() {
        Executors.newSingleThreadExecutor().execute(() -> {
            ApiService.apiService.getConversations().enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<List<Conversation>> call, @NonNull Response<List<Conversation>> response) {

                    if (response.isSuccessful() && response.code() == 200) {
                        List<Conversation> conversations = response.body();
                        if (conversations != null && conversations.size() != 0) {
                            conversations.forEach(conversation -> {
//                                if (conversation.getLastMessage() != null)
                                    viewModel.insertOrUpdate(conversation);
                            });
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Conversation>> call, @NonNull Throwable t) {
                    Log.e("API_ERR", t.getLocalizedMessage());
                }
            });
        });
    }

    private void tapToCloseApp() {
        mBinding.mBtnCloseSearch.setOnClickListener(view -> {
            mMainActivity.onBackPressed();
        });
    }

    private void addMoreFriendEvent() {
        mBinding.mBtnAddFriend.setOnClickListener(view -> {
            popupMenu.show();
        });
    }

    private void initPopupMenu() {
        popupMenu = new PopupMenu(mMainActivity, mBinding.mBtnAddFriend, Gravity.END, 0, R.style.MyPopupMenu);
        popupMenu.setForceShowIcon(true);
        popupMenu.getMenuInflater().inflate(R.menu.feature_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_new_group)
                mMainActivity.addFragmentToBackStack(new AddNewGroupFragment());
            else if (item.getItemId() == R.id.action_new_friend)
                mMainActivity.addFragmentToBackStack(new AddFriendFragment());

            return true;
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

//                        Log.e("TEST add", conversation.toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}