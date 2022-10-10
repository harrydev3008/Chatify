package com.hisu.zola.fragments.contact;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.ContactViewPagerAdapter;
import com.hisu.zola.databinding.FragmentContactsBinding;

public class ContactsFragment extends Fragment {

    private FragmentContactsBinding mBinding;
    private MainActivity mMainActivity;
    private ContactViewPagerAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainActivity = (MainActivity) getActivity();
        mBinding = FragmentContactsBinding.inflate(inflater, container, false);

        tapToCloseApp();
        initTabLabLayout();

        return mBinding.getRoot();
    }

    private void initTabLabLayout() {
        adapter = new ContactViewPagerAdapter(mMainActivity);
        mBinding.vpContact.setAdapter(adapter);
        mBinding.vpContact.setUserInputEnabled(false);

        new TabLayoutMediator(mBinding.tlContact, mBinding.vpContact, (tab, position) -> {
            switch (position) {
                case 0: {
                    tab.setText(getString(R.string.tab_contact_friend));
                    break;
                }

                case 1: {
                    tab.setText(getString(R.string.tab_contact_group));
                    break;
                }
            }
        }).attach();
    }

    private void tapToCloseApp() {
        mBinding.mBtnBack.setOnClickListener(view -> {
            mMainActivity.onBackPressed();
        });
    }
}