package com.example.mislugares.models;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by vicch on 11/02/2018.
 */

public class LugaresFirebase implements LugaresAsinc{

    private final static String NODO_LUGARES = "lugares";
    private DatabaseReference nodo;

    public LugaresFirebase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        nodo = database.getReference().child(NODO_LUGARES);
    }

    @Override
    public void elemento(String id, final EscuchadorElemento escuchador) {
        nodo.child(id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Lugar lugar = dataSnapshot.getValue(Lugar.class);
                        escuchador.onRespuesta(lugar);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Firebase","Error al leer", databaseError.toException());
                        escuchador.onRespuesta(null);
                    }
                }
        );
    }

    @Override
    public void anyade(Lugar lugar) {
        nodo.push().setValue(lugar);
    }

    @Override
    public String nuevo() {
        return nodo.push().getKey();
    }

    @Override
    public void borrar(String id) {
        nodo.child(id).setValue(null);
    }

    @Override
    public void actualiza(String id, Lugar lugar) {
        nodo.child(id).setValue(lugar);
    }

    @Override
    public void tamanyo(final EscuchadorTamanyo escuchador) {
        nodo.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        escuchador.onRespuesta(dataSnapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Firebase", "Error en tamanyo.", databaseError.toException());
                        escuchador.onRespuesta(-1);
                    }
                }
        );
    }
}
