package com.example.braintrainer;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.braintrainer.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int defaultTime = 30;

    private SharedPreferences appSettings;

    private void initialAppSettings()
    {
        int time = appSettings.getInt("time", -1);
        SharedPreferences.Editor editor = appSettings.edit();

        if(time < 0)
        {
            editor.putInt("time", defaultTime);
            editor.apply();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.braintrainer.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_play, R.id.navigation_settings, R.id.navigation_records)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
        appSettings = getSharedPreferences("com.example.braintrainer", MODE_PRIVATE);

        initialAppSettings();
    }
}