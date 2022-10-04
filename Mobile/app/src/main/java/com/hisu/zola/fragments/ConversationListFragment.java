package com.hisu.zola.fragments;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.ConversationAdapter;
import com.hisu.zola.databinding.FragmentConversationListBinding;
import com.hisu.zola.entity.ConversationHolder;

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

        backToPrevPage();

        toggleShowClearIconOnSearchEditText();
        clearTextOnSearchEditText();
        addMoreFriendEvent();

        return mBinding.getRoot();
    }

    private void initConversationListRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                mMainActivity, RecyclerView.VERTICAL, false
        );

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

        adapter.setOnConversationItemSelectedListener(conversationID ->
                mMainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(
                                mMainActivity.getViewContainerID(),
                                ConversationFragment.newInstance(conversationID)
                        )
                        .addToBackStack("Single_Conversation")
                        .commit());

        mBinding.rvConversationList.setAdapter(adapter);
    }

    private void backToPrevPage() {
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
            //Todo: add method allow user to add more friend by phone number or username, etc..
            Toast.makeText(mMainActivity, "Function not available right now!", Toast.LENGTH_SHORT).show();
        });
    }
}