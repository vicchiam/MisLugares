package com.example.mislugares.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.mislugares.fragments.PreferenciasFragment;

public class PreferenciasActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenciasFragment())
                .commit();
    }
}