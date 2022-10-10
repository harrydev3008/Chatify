package com.hisu.zola.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.badge.BadgeDrawable;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentHomeBinding;
import com.hisu.zola.fragments.contact.ContactsFragment;
import com.hisu.zola.fragments.conversation.ConversationListFragment;
import com.hisu.zola.fragments.profile.ProfileFragment;

public class HomeFragment extends Fragment {

    public static final String BACK_FROM_EDIT_ARGS = "BACK_FROM_EDIT_MODE";
    public static final String BACK_FROM_CONTACT_ARGS = "BACK_FROM_CONTACT_MODE";
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
        }  else if (getArguments().getString(MODE).equalsIgnoreCase(BACK_FROM_CONTACT_ARGS)) {
            setChildrenFragment(new ContactsFragment(), "Contacts");
            mBinding.navigationMenu.setSelectedItemId(R.id.action_contact);
        }
        else if (getArguments().getString(MODE).equalsIgnoreCase(NORMAL_ARGS)) {
            setChildrenFragment(new ConversationListFragment(), "Conversation");
            mBinding.navigationMenu.setSelectedItemId(R.id.action_message);
        }

        addSelectedActionForNavItem();

        messageBadge();

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

//  Todo: add method to observe unread msg from db
    private void messageBadge() {
        BadgeDrawable badge = mBinding.navigationMenu.getOrCreateBadge(R.id.action_message);
        badge.setNumber(5);
        badge.setBackgroundColor(ContextCompat.getColor(mMainActivity, R.color.chat_badge_bg));
        badge.setVerticalOffset(10);
        badge.setHorizontalOffset(5);
    }
}