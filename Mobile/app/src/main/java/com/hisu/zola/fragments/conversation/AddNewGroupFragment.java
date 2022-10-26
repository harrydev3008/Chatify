package com.hisu.zola.fragments.conversation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hisu.zola.MainActivity;
import com.hisu.zola.adapters.AddGroupMemberAdapter;
import com.hisu.zola.databinding.FragmentAddNewGroupBinding;
import com.hisu.zola.database.entity.User;
import com.hisu.zola.util.local.LocalDataManager;

import java.util.ArrayList;
import java.util.List;

public class AddNewGroupFragment extends Fragment {

    private FragmentAddNewGroupBinding mBinding;
    private MainActivity mainActivity;
    private List<String> members;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentAddNewGroupBinding.inflate(inflater, container, false);

        mainActivity.setBottomNavVisibility(View.GONE);

        init();
        addActionForBtnCancel();
        addActionForBtnDone();

        return mBinding.getRoot();
    }

    private void init() {

        members = new ArrayList<>();

        User user = LocalDataManager.getCurrentUserInfo();
        AddGroupMemberAdapter adapter = new AddGroupMemberAdapter(
                List.of(user, user, user, user), mainActivity
        );

        adapter.setOnItemCheckedChangListener((userID, isCheck) -> {
            if (isCheck)
                members.add(userID);
            else
                members.remove(userID);

            Toast.makeText(mainActivity, userID, Toast.LENGTH_SHORT).show();

            if (members.size() > 0)
                mBinding.iBtnDone.setVisibility(View.VISIBLE);
            else
                mBinding.iBtnDone.setVisibility(View.GONE);
        });

        mBinding.rvFriends.setAdapter(adapter);
        mBinding.rvFriends.setLayoutManager(new LinearLayoutManager(mainActivity));
    }

    private void addActionForBtnCancel() {
        mBinding.iBtnCancel.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.VISIBLE);
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }

    private void addActionForBtnDone() {
        mBinding.iBtnDone.setOnClickListener(view -> {
            mainActivity.setBottomNavVisibility(View.VISIBLE);
            mainActivity.getSupportFragmentManager().popBackStackImmediate();
        });
    }
}