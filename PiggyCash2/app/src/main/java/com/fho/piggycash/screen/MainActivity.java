package com.fho.piggycash.screen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.fho.piggycash.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
    }
}