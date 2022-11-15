package com.hisu.zola.fragments.conversation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.SentFileAdapter;
import com.hisu.zola.database.entity.Conversation;
import com.hisu.zola.database.entity.Message;
import com.hisu.zola.database.repository.MessageRepository;
import com.hisu.zola.databinding.FragmentSentFilesBinding;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.List;

public class SentFilesFragment extends Fragment {

    public static final String CONVERSATION_VIEW_FILES_ARGS = "CONVERSATION_VIEW_FILES_ARGS";

    private FragmentSentFilesBinding mBinding;
    private MainActivity mainActivity;
    private Conversation conversation;
    private SentFileAdapter sentFileAdapter;
    private MessageRepository repository;

    public static SentFilesFragment newInstance(Conversation conversation) {
        Bundle args = new Bundle();
        args.putSerializable(CONVERSATION_VIEW_FILES_ARGS, conversation);
        SentFilesFragment fragment = new SentFilesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if(getArguments() != null) {
            conversation = (Conversation) getArguments().getSerializable(CONVERSATION_VIEW_FILES_ARGS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentSentFilesBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new MessageRepository(mainActivity.getApplication());
        backToPrevPage();
        initRecyclerView();
    }

    private void backToPrevPage() {
        mBinding.iBtnBack.setOnClickListener(view -> {
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void initRecyclerView() {
        sentFileAdapter = new SentFileAdapter(mainActivity);

        repository.getData(conversation.getId()).observe(mainActivity, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                if(messages == null) return;
                if(messages.isEmpty()) return;
//                messages.addAll(messages);
                sentFileAdapter.setMessages(messages);
                mBinding.rvSentFiles.setAdapter(sentFileAdapter);
            }
        });

        mBinding.rvSentFiles.setLayoutManager(new LinearLayoutManager(mainActivity));
        mBinding.rvSentFiles.addItemDecoration(new StickyRecyclerHeadersDecoration(sentFileAdapter));
    }
}