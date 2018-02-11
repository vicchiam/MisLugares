package com.example.mislugares.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mislugares.activities.MainActivity;
import com.example.mislugares.R;
import com.example.mislugares.adapters.AdaptadorLugaresFirebase;
import com.example.mislugares.adapters.AdaptadorLugaresFirebaseUI;
import com.example.mislugares.models.Lugar;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Created by Jesús Tomás on 19/04/2017.
 */

public class SelectorFragment extends Fragment {
    private RecyclerView recyclerView;

    public static AdaptadorLugaresFirebaseUI adaptador;
    //public static AdaptadorLugaresFirebase adaptador;

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor,
                             Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_selector,
                contenedor, false);
        recyclerView =(RecyclerView) vista.findViewById(R.id.recycler_view);
        return vista;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setAutoMeasureEnabled(true); //Quitar si da problemas

        //Firebase adapter
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("lugares")
                .limitToLast(50);
        FirebaseRecyclerOptions<Lugar> opciones = new FirebaseRecyclerOptions
                .Builder<Lugar>()
                .setQuery(query, Lugar.class).build();
                adaptador = new AdaptadorLugaresFirebaseUI(opciones);
                //adaptador = new AdaptadorLugaresFirebase(this.getActivity(),FirebaseDatabase.getInstance().getReference());
                recyclerView.setAdapter(adaptador);
        adaptador.startListening();
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ((MainActivity) getActivity()).muestraLugar(
                        recyclerView.getChildAdapterPosition(v));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //adaptador.startListening(); //Se recomienda ponerlo, pero no se va ha hacer
    }

    @Override
    public void onStop() {
        super.onStop();
        //adaptador.stopListening(); //Se recomienda ponerlo, pero no se va ha hacer
    }

    @Override public void onDestroy() {
        super.onDestroy();
        adaptador.stopListening();
    }

}