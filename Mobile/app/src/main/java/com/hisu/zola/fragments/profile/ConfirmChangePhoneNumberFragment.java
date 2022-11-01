package com.hisu.zola.fragments.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentConfirmChangePhoneNumberBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backToPrevPage();
        startChangePhoneNumber();
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