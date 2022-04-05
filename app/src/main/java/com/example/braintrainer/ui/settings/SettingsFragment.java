package com.example.braintrainer.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.braintrainer.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    private SharedPreferences appSettings;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        appSettings = requireActivity().getSharedPreferences("com.example.braintrainer",
                Context.MODE_PRIVATE);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        final int currentTime = appSettings.getInt("time", -1);

        binding.timeSettingSeekBar.setProgress(currentTime);
        binding.timeSettingSeekBar.setMax(120);
        binding.timeSettingsTextView.setText(currentTime + "s");

        binding.timeSettingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(progress < 10)
                {
                    binding.timeSettingSeekBar.setProgress(10);
                }else {
                    binding.timeSettingsTextView.setText(progress + "s");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final int currentSetTime = seekBar.getProgress();
                appSettings.edit().putInt("time", currentSetTime).apply();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}