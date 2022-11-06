package com.fho.piggycash.screen.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.ImageView;

import com.fho.piggycash.R;
import com.fho.piggycash.screen.fragment.FragmentMain;
import com.fho.piggycash.screen.fragment.FragmentProfile;
import com.fho.piggycash.service.AppService;

import java.util.Objects;

public class ActivityMain extends AppCompatActivity {

    private final AppService appService = AppService.getInstance();

    private ImageView img_profile, img_home;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        initComponents();

        img_home.setOnClickListener(v -> mainFragment());
        img_profile.setOnClickListener(v -> profileFragment());
    }

    private void mainFragment() {
        fm.beginTransaction()
                .replace(R.id.fragment_view, FragmentMain.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    private void profileFragment() {
        fm.beginTransaction()
                .replace(R.id.fragment_view, FragmentProfile.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    private void loadImage(){
        img_profile = findViewById(R.id.img_main_profile);
        appService.getProfileImage(img_profile);
    }

    private void initComponents(){
        loadImage();
        fm = getSupportFragmentManager();
        img_home = findViewById(R.id.img_home);
        mainFragment();
    }
}