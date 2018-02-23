package com.example.mislugares.adapters;

import android.view.View;

import com.example.mislugares.models.Lugar;

/**
 * Created by vicch on 14/02/2018.
 */

public interface AdaptadorLugaresInterface {

    public String getKey(int pos);
    public Lugar getItem(int pos);
    public void setOnItemClickListener(View.OnClickListener onClick);
    public void startListening();
    public void stopListening();

}
