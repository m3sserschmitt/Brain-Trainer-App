package com.example.braintrainer;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.braintrainer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final int defaultTime = 10;
    private static final int defaultMode = 0;

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

        int mode = appSettings.getInt("mode", -1);

        if(mode < 0)
        {
            editor.putInt("mode", defaultMode);
            editor.apply();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.braintrainer.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        appSettings = getSharedPreferences("com.example.braintrainer", MODE_PRIVATE);

        initialAppSettings();
    }
}