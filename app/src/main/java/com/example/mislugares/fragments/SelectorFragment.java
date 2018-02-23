package com.example.mislugares.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mislugares.activities.MainActivity;
import com.example.mislugares.R;
import com.example.mislugares.adapters.AdaptadorLugares;
import com.example.mislugares.adapters.AdaptadorLugaresFirebase;
import com.example.mislugares.adapters.AdaptadorLugaresFirebaseUI;
import com.example.mislugares.adapters.AdaptadorLugaresFirestore;
import com.example.mislugares.adapters.AdaptadorLugaresFirestoreUI;
import com.example.mislugares.adapters.AdaptadorLugaresInterface;
import com.example.mislugares.models.Lugar;
import com.example.mislugares.utilities.Preferencias;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.mislugares.utilities.Preferencias.SELECCION_MIOS;
import static com.example.mislugares.utilities.Preferencias.SELECCION_TIPO;

/**
 * Created by Jesús Tomás on 19/04/2017.
 */

public class SelectorFragment extends Fragment {
    private static RecyclerView recyclerView;
    private static Context context;

    public static RecyclerView.Adapter adaptador;

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

        context = getContext();
        ponerAdaptador();

    }

    public static void ponerAdaptador() {
        Preferencias pref = Preferencias.getInstance();
        pref.inicializa(context);

        if (pref.usarFirestore()) {
            Log.d("TIPO", pref.criterioOrdenacion());
            com.google.firebase.firestore.Query query = FirebaseFirestore.getInstance()
                    .collection("lugares")
                    .orderBy(pref.criterioOrdenacion())
                    .limit(pref.maximoMostrar());

            switch (pref.criterioSeleccion()){
                case SELECCION_MIOS:
                    query = query.whereEqualTo("creador", FirebaseAuth.getInstance()
                            .getCurrentUser().getUid());
                    break;
                case SELECCION_TIPO:
                    query = query.whereEqualTo("tipo", pref.tipoSeleccion());
                    break;
            }

            if (pref.usarFirebaseUI()) {
                FirestoreRecyclerOptions<Lugar> options = new FirestoreRecyclerOptions.Builder<Lugar>()
                        .setQuery(query, Lugar.class)
                        .build();
                adaptador = new AdaptadorLugaresFirestoreUI(options);
            }
            else {
                adaptador = new AdaptadorLugaresFirestore(context, query);
            }
        }
        else {
            if (pref.usarFirebaseUI()) {
                com.google.firebase.database.Query query = FirebaseDatabase
                        .getInstance().getReference().child("lugares")
                        .limitToLast(pref.maximoMostrar());
                switch (pref.criterioSeleccion()) {
                    case SELECCION_MIOS:
                        query = query.orderByChild("creador")
                                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        break;
                    case SELECCION_TIPO:
                        query = query.orderByChild("tipo")
                                .equalTo(pref.tipoSeleccion());
                        break;
                    default:
                        query = query.orderByChild(pref.criterioOrdenacion());
                        break;
                }
                FirebaseRecyclerOptions<Lugar> options = new FirebaseRecyclerOptions
                        .Builder<Lugar>()
                        .setQuery(query, Lugar.class)
                        .build();
                adaptador = new AdaptadorLugaresFirebaseUI(options);
            }
            else {
                adaptador = new AdaptadorLugaresFirebase(context, FirebaseDatabase.getInstance().getReference("lugares"));
            }
        }
        getAdaptador().setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).muestraLugar( recyclerView.getChildAdapterPosition(v));
            }
        });

        recyclerView.setAdapter(adaptador);
        getAdaptador().startListening();
    }

    public static AdaptadorLugaresInterface getAdaptador() {
        return (AdaptadorLugaresInterface) adaptador;
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
        getAdaptador().stopListening();
    }

}