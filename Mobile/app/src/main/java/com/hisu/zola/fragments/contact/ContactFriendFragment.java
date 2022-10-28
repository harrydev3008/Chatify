package com.hisu.zola.fragments.contact;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.ConversationAdapter;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.databinding.FragmentContactFriendBinding;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.util.ApiService;
import com.hisu.zola.view_model.ConversationListViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactFriendFragment extends Fragment {

    private FragmentContactFriendBinding mBinding;
    private MainActivity mainActivity;
    private ConversationAdapter adapter;
    private ConversationListViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();

        mBinding = FragmentContactFriendBinding.inflate(inflater, container, false);

        showFriendRequestList();
        showFriendFromContact();

        initConversationListRecyclerView();

        return mBinding.getRoot();
    }

    private void showFriendRequestList() {
        mBinding.acFriendRequest.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.GONE);
            mainActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left, R.anim.slide_out_left,
                            R.anim.slide_out_right, R.anim.slide_out_right)
                    .replace(
                            mainActivity.getViewContainerID(),
                            new FriendRequestFragment()
                    )
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void showFriendFromContact() {
        mBinding.acFriendFromContact.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.GONE);
            mainActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left, R.anim.slide_out_left,
                            R.anim.slide_out_right, R.anim.slide_out_right)
                    .replace(
                            mainActivity.getViewContainerID(),
                            new FriendFromContactFragment()
                    )
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void initConversationListRecyclerView() {
        viewModel = new ViewModelProvider(mainActivity).get(ConversationListViewModel.class);
        adapter = new ConversationAdapter(mainActivity);

        viewModel.getData().observe(mainActivity, new Observer<List<Conversation>>() {
            @Override
            public void onChanged(List<Conversation> conversations) {
                List<Conversation> friendConversations = new ArrayList<>();

                conversations.forEach(conversation -> {
                    if(conversation.getLabel() == null || conversation.getLabel().isEmpty())
                        friendConversations.add(conversation);
                });

                adapter.setConversations(friendConversations);
                mBinding.rvFriend.setAdapter(adapter);
            }
        });

//        adapter.setOnConversationItemSelectedListener((conversation, conversationName) -> {
//            mainActivity.setBottomNavVisibility(View.GONE);
//            mainActivity.getSupportFragmentManager().beginTransaction()
//                    .setCustomAnimations(
//                            R.anim.slide_in_left, R.anim.slide_out_left,
//                            R.anim.slide_out_right, R.anim.slide_out_right)
//                    .add(
//                            mainActivity.getViewContainerID(),
//                            ConversationFragment.newInstance(conversation, conversationName)
//                    )
//                    .addToBackStack("Single_Conversation")
//                    .commit();
//        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                mainActivity, RecyclerView.VERTICAL, false
        );

        mBinding.rvFriend.setLayoutManager(linearLayoutManager);

        loadConversationList();
    }

    private void loadConversationList() {

        ApiService.apiService.getAllFriends().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful() && response.code() == 200) {
                    List<User> users = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("API_ERR", t.getLocalizedMessage());
            }
        });
    }
}