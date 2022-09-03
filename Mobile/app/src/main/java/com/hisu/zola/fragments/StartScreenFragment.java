package com.hisu.zola.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hisu.zola.MainActivity;
import com.hisu.zola.R;
import com.hisu.zola.adapters.StarterSliderAdapter;
import com.hisu.zola.databinding.FragmentStartScreenBinding;

import java.util.List;

public class StartScreenFragment extends Fragment {

    private FragmentStartScreenBinding binding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStartScreenBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();

        binding.vpStarter.setAdapter(
                new StarterSliderAdapter(
                        List.of(R.mipmap.ic_launcher, R.mipmap.launcher_icon, R.mipmap.ic_launcher_round)
                )
        );

        binding.circleIndicator.setViewPager(binding.vpStarter);

        binding.btnLogin.setOnClickListener(view -> {
            mainActivity.setFragment(new LoginFragment());
        });

        binding.btnRegister.setOnClickListener(view -> {
            mainActivity.setFragment(new RegisterFragment());
        });

        return binding.getRoot();
    }
}