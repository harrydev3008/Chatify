package com.hisu.zola.fragments.conversation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.AddGroupMemberAdapter;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.databinding.FragmentAddMemberToGroupBinding;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.ArrayList;
import java.util.List;

public class AddMemberToGroupFragment extends Fragment {

    private FragmentAddMemberToGroupBinding mBinding;
    private MainActivity mainActivity;
    private List<String> members;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentAddMemberToGroupBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addActionForBtnCancel();
        init();
        addActionForBtnCancel();
        addActionForBtnDone();
    }

    private void init() {

        members = new ArrayList<>();

        User user = LocalDataManager.getCurrentUserInfo();
        AddGroupMemberAdapter adapter = new AddGroupMemberAdapter(
                user.getFriends(), mainActivity
        );

        adapter.setOnItemCheckedChangListener((userID, isCheck) -> {
            if (isCheck)
                members.add(userID);
            else
                members.remove(userID);

            if (members.size() > 0)
                mBinding.iBtnDone.setVisibility(View.VISIBLE);
            else
                mBinding.iBtnDone.setVisibility(View.GONE);
        });

        mBinding.rvMembers.setAdapter(adapter);
        mBinding.rvMembers.setLayoutManager(new LinearLayoutManager(mainActivity));
    }

    private void addActionForBtnCancel() {
        mBinding.iBtnCancel.setOnClickListener(view -> {
            if (!isDataChanged()) {
                backToPrevPage();
            } else {
                new AlertDialog.Builder(mainActivity)
                        .setMessage(getString(R.string.changes_not_save))
                        .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> backToPrevPage())
                        .setNegativeButton(getString(R.string.no), null).show();
            }
        });
    }

    private void backToPrevPage() {
        mainActivity.setBottomNavVisibility(View.GONE);
        mainActivity.getSupportFragmentManager().popBackStackImmediate();
    }

    private void addActionForBtnDone() {
        mBinding.iBtnDone.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.VISIBLE);
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private boolean isDataChanged() {
        return members.size() > 1;
    }
}