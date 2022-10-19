package com.hisu.zola.fragments.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentConfirmChangePhoneNumberBinding;

public class ConfirmChangePhoneNumberFragment extends Fragment {

    private FragmentConfirmChangePhoneNumberBinding mBinding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        mBinding = FragmentConfirmChangePhoneNumberBinding.inflate(inflater, container, false);

        backToPrevPage();
        startChangePhoneNumber();

        return mBinding.getRoot();
    }

    private void backToPrevPage() {
       mBinding.iBtnBack.setOnClickListener(view -> {
           mainActivity.getSupportFragmentManager().popBackStackImmediate();
       });
    }

    private void startChangePhoneNumber() {
        mBinding.tvStart.setOnClickListener(view -> {
            mainActivity.addFragmentToBackStack(new ChangePhoneNumberFragment());
        });
    }
}