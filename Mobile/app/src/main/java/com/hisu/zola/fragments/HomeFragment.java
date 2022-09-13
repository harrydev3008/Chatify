package com.hisu.zola.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;
import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.databinding.FragmentHomeBinding;
import com.hisu.zola.util.NotificationUtil;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        addSelectedActionForNavItem();

        return binding.getRoot();
    }

    @SuppressLint("NonConstantResourceId")
    private void addSelectedActionForNavItem() {
        binding.navigationMenu.setOnItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.action_message: {
                    Toast.makeText(mainActivity, "Message", Toast.LENGTH_SHORT).show();
                }

                case R.id.action_contact: {
                    Toast.makeText(mainActivity, "Contacts", Toast.LENGTH_SHORT).show();
                }

                case R.id.action_profile: {
                    Toast.makeText(mainActivity, "Profile", Toast.LENGTH_SHORT).show();
                }
            }

            return true;
        });
    }
}