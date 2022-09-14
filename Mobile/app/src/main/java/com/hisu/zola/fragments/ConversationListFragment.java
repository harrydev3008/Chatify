package com.hisu.zola.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.ConversationAdapter;
import com.hisu.zola.databinding.FragmentConversationListBinding;
import com.hisu.zola.entity.ConversationHolder;
import com.hisu.zola.listeners.IOnConversationItemSelectedListener;

import java.util.List;

public class ConversationListFragment extends Fragment {

    private FragmentConversationListBinding mBinding;
    private MainActivity mMainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainActivity = (MainActivity) getActivity();
        mBinding = FragmentConversationListBinding.inflate(inflater, container, false);

        initConversationListRecyclerView();
        loadConversationList();

        return mBinding.getRoot();
    }

    private void initConversationListRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                mMainActivity, RecyclerView.VERTICAL, false
        );

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mBinding.rvConversationList.getContext(),
                linearLayoutManager.getOrientation()
        );

        mBinding.rvConversationList.addItemDecoration(dividerItemDecoration);
        mBinding.rvConversationList.setLayoutManager(linearLayoutManager);
    }

    private void loadConversationList() {
        ConversationAdapter adapter = new ConversationAdapter(
                List.of(
                        new ConversationHolder("1", false, R.mipmap.app_launcher_icon, "Harry Nguyen",
                                "Em ăn cơm chưa?", 1),
                        new ConversationHolder("2", false, R.mipmap.app_launcher_icon, "John Doe",
                                "Dude? why not reply?", 2),
                        new ConversationHolder("3", false, R.mipmap.app_launcher_icon, "Marry Jane",
                                "Harry?", 1),
                        new ConversationHolder("4", false, R.mipmap.app_launcher_icon, "Peta Parker",
                                "Wanna put some dirt in her eyes?", 0)
                ), mMainActivity
        );

        adapter.setOnConversationItemSelectedListener(new IOnConversationItemSelectedListener() {
            @Override
            public void openConversation(String conversationID) {
                mMainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(
                                mMainActivity.getViewContainerID(),
                                ConversationFragment.newInstance(conversationID)
                        )
                        .addToBackStack("Single_Conversation")
                        .commit();
            }
        });

        mBinding.rvConversationList.setAdapter(adapter);
    }
}