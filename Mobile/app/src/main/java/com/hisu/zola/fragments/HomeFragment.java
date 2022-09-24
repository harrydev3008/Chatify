package com.hisu.zola.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    public static final String BACK_FROM_EDIT_ARGS = "BACK_FROM_MODE";
    public static final String NORMAL_ARGS = "NORMAL_MODE";
    public static final String MODE = "MODE";

    private FragmentHomeBinding mBinding;
    private MainActivity mMainActivity;

    public static HomeFragment newInstance(String mode) {
        Bundle args = new Bundle();
        args.putString(MODE, mode);

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainActivity = (MainActivity) getActivity();
        mBinding = FragmentHomeBinding.inflate(inflater, container, false);

        if (getArguments().getString(MODE).equalsIgnoreCase(BACK_FROM_EDIT_ARGS)) {
            setChildrenFragment(new ProfileFragment(), "Conversation");
            mBinding.navigationMenu.setSelectedItemId(R.id.action_profile);
        } else if (getArguments().getString(MODE).equalsIgnoreCase(NORMAL_ARGS)) {
            setChildrenFragment(new ConversationListFragment(), "Conversation");
            mBinding.navigationMenu.setSelectedItemId(R.id.action_message);
        }

        addSelectedActionForNavItem();

        return mBinding.getRoot();
    }

    @SuppressLint("NonConstantResourceId")
    private void addSelectedActionForNavItem() {
        mBinding.navigationMenu.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_message: {
                    setChildrenFragment(new ConversationListFragment(), "Conversation");
                    break;
                }

                case R.id.action_contact: {
                    setChildrenFragment(new ContactsFragment(), "Contacts");
                    break;
                }

                case R.id.action_profile: {
                    setChildrenFragment(new ProfileFragment(), "Profile");
                    break;
                }
            }

            return true;
        });
    }

    private void setChildrenFragment(Fragment fragment, String backStackName) {
        getChildFragmentManager().beginTransaction()
                .replace(mBinding.homeViewContainer.getId(), fragment)
                .addToBackStack(backStackName)
                .commit();
    }
}