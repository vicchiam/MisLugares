package com.example.mislugares.adapters;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mislugares.R;
import com.example.mislugares.models.Lugar;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * Created by vicch on 14/02/2018.
 */

public class AdaptadorLugaresFirestoreUI extends FirestoreRecyclerAdapter<Lugar, AdaptadorLugares.ViewHolder> implements AdaptadorLugaresInterface {

    protected View.OnClickListener onClickListener;

    public AdaptadorLugaresFirestoreUI( @NonNull FirestoreRecyclerOptions<Lugar> options) {
        super(options);
        Log.d("TIPO", "Firestore UI");

    }

    @Override
    public AdaptadorLugares.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elemento_lista, parent, false);
        return new AdaptadorLugares.ViewHolder(view);

    }

    @Override
    protected void onBindViewHolder(@NonNull AdaptadorLugares .ViewHolder holder, int position, @NonNull Lugar lugar) {
        AdaptadorLugares.personalizaVista(holder, lugar);
        holder.itemView.setOnClickListener(onClickListener);
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }

    public String getKey(int pos) {
        return super.getSnapshots().getSnapshot(pos).getId();
    }

}
