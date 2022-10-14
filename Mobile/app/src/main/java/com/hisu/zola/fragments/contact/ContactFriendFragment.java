package com.hisu.zola.fragments.contact;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.ConversationAdapter;
import com.hisu.zola.databinding.FragmentContactFriendBinding;
import com.hisu.zola.fragments.conversation.ConversationFragment;
import com.hisu.zola.view_model.ConversationListViewModel;

public class ContactFriendFragment extends Fragment {

    private FragmentContactFriendBinding mBinding;
    private MainActivity mainActivity;
    private ConversationListViewModel viewModel;
    private ConversationAdapter adapter;

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
            mainActivity.setFragment(new FriendRequestFragment());
        });
    }

    private void showFriendFromContact() {
        mBinding.acFriendFromContact.setOnClickListener(view -> {
            Toast.makeText(mainActivity, "friend from contact", Toast.LENGTH_SHORT).show();
        });
    }

    private void initConversationListRecyclerView() {
        viewModel = new ViewModelProvider(mainActivity).get(ConversationListViewModel.class);

        adapter = new ConversationAdapter(mainActivity);
        adapter.setOnConversationItemSelectedListener(conversationID -> {
            mainActivity.setBottomNavVisibility(View.GONE);
            mainActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left, R.anim.slide_out_left,
                            R.anim.slide_out_right, R.anim.slide_out_right)
                    .add(
                            mainActivity.getViewContainerID(),
                            ConversationFragment.newInstance(conversationID)
                    )
                    .addToBackStack("Single_Conversation")
                    .commit();
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                mainActivity, RecyclerView.VERTICAL, false
        );

        mBinding.rvFriend.setLayoutManager(linearLayoutManager);

        loadConversationList();
    }

    private void loadConversationList() {
        viewModel.getData().observe(mainActivity, conversation -> {
            adapter.setConversations(conversation);
            mBinding.rvFriend.setAdapter(adapter);
        });
    }
}