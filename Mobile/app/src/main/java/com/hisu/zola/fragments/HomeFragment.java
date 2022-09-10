package com.hisu.zola.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentHomeBinding;
import com.hisu.zola.util.NotificationUtil;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        binding.btnSendMsg.setOnClickListener(view -> {
            NotificationUtil.pushNotification(mainActivity, getString(R.string.system_noty_channel_id),
                    "Test user's message notification ehe!");
        });

        return binding.getRoot();
    }
}