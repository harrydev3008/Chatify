package com.hisu.zola.fragments.conversation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.ConversationAdapter;
import com.hisu.zola.databinding.FragmentConversationListBinding;
import com.hisu.zola.fragments.AddFriendFragment;
import com.hisu.zola.view_model.ConversationListViewModel;

public class ConversationListFragment extends Fragment {

    private FragmentConversationListBinding mBinding;
    private MainActivity mMainActivity;
    private ConversationListViewModel viewModel;
    private ConversationAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainActivity = (MainActivity) getActivity();
        mBinding = FragmentConversationListBinding.inflate(inflater, container, false);

        initConversationListRecyclerView();

        tapToCloseApp();
        toggleShowClearIconOnSearchEditText();
        clearTextOnSearchEditText();
        addMoreFriendEvent();

        return mBinding.getRoot();
    }

    private void initConversationListRecyclerView() {
        viewModel = new ViewModelProvider(mMainActivity).get(ConversationListViewModel.class);

        adapter = new ConversationAdapter(mMainActivity);
        adapter.setOnConversationItemSelectedListener(conversationID -> {
            mMainActivity.setBottomNavVisibility(View.GONE);
            mMainActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left, R.anim.slide_out_left,
                            R.anim.slide_out_right, R.anim.slide_out_right)
                    .replace(
                            mMainActivity.getViewContainerID(),
                            ConversationFragment.newInstance(conversationID)
                    )
                    .addToBackStack("Single_Conversation")
                    .commit();
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                mMainActivity, RecyclerView.VERTICAL, false
        );

        mBinding.rvConversationList.setLayoutManager(linearLayoutManager);

        loadConversationList();
    }

    private void loadConversationList() {
        viewModel.getData().observe(mMainActivity, conversation -> {
            adapter.setConversations(conversation);
            mBinding.rvConversationList.setAdapter(adapter);
        });
    }

    private void tapToCloseApp() {
        mBinding.mBtnCloseSearch.setOnClickListener(view -> {
            mMainActivity.onBackPressed();
        });
    }

    private void toggleShowClearIconOnSearchEditText() {
        mBinding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    mBinding.edtSearch.setCompoundDrawablesWithIntrinsicBounds(
                            null, null,
                            ContextCompat.getDrawable(
                                    mMainActivity, R.drawable.ic_close), null
                    );
                else
                    mBinding.edtSearch.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, null, null
                    );
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void clearTextOnSearchEditText() {
        mBinding.edtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mBinding.edtSearch.getCompoundDrawables()[2] == null) return false;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mBinding.edtSearch.getRight() -
                            mBinding.edtSearch.getCompoundDrawables()[2]
                                    .getBounds().width())) {

                        mBinding.edtSearch.setText("");

                        return true;
                    }
                }

                return false;
            }
        });
    }

    private void addMoreFriendEvent() {
        mBinding.mBtnAddFriend.setOnClickListener(view -> {
            mMainActivity.setBottomNavVisibility(View.GONE);
            mMainActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left, R.anim.slide_out_left,
                            R.anim.slide_out_right, R.anim.slide_out_right)
                    .replace(
                            mMainActivity.getViewContainerID(),
                            new AddFriendFragment()
                    )
                    .addToBackStack(null)
                    .commit();
        });
    }
}