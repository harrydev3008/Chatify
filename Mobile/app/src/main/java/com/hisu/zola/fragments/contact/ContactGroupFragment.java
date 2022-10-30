package com.hisu.zola.fragments.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.ConversationAdapter;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.databinding.FragmentContactGroupBinding;
import com.hisu.zola.fragments.conversation.AddNewGroupFragment;
import com.hisu.zola.fragments.conversation.ConversationFragment;
import com.hisu.zola.view_model.ConversationListViewModel;

import java.util.ArrayList;
import java.util.List;

public class ContactGroupFragment extends Fragment {

    private FragmentContactGroupBinding mBinding;
    private MainActivity mainActivity;
    private ConversationListViewModel viewModel;
    private ConversationAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentContactGroupBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initConversationListRecyclerView();
        addNewGroupEvent();
    }

    private void initConversationListRecyclerView() {
        viewModel = new ViewModelProvider(mainActivity).get(ConversationListViewModel.class);
        adapter = new ConversationAdapter(mainActivity);

        viewModel.getData().observe(mainActivity, new Observer<List<Conversation>>() {
            @Override
            public void onChanged(List<Conversation> conversations) {
                List<Conversation> groupConversations = new ArrayList<>();

                conversations.forEach(conversation -> {
                    if (conversation.getLabel() != null)
                        groupConversations.add(conversation);
                });

                adapter.setConversations(groupConversations);
                mBinding.rvGroup.setAdapter(adapter);
            }
        });

        adapter.setOnConversationItemSelectedListener((conversationID, conversationName) -> {
            mainActivity.setBottomNavVisibility(View.GONE);
            mainActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left, R.anim.slide_out_left,
                            R.anim.slide_out_right, R.anim.slide_out_right)
                    .add(
                            mainActivity.getViewContainerID(),
                            ConversationFragment.newInstance(conversationID, conversationName)
                    )
                    .addToBackStack("Group_Conversation")
                    .commit();
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                mainActivity, RecyclerView.VERTICAL, false
        );

        mBinding.rvGroup.setLayoutManager(linearLayoutManager);
    }

    private void addNewGroupEvent() {
        mBinding.acNewGroup.setOnClickListener(view -> {
            mainActivity.addFragmentToBackStack(new AddNewGroupFragment());
        });
    }
}