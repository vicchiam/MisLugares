package com.example.mislugares.models;

import android.content.Context;

import com.example.mislugares.utilities.Preferencias;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by vicch on 11/02/2018.
 */

public class Usuarios {

    public static void guardarUsuario(final FirebaseUser user, Context context) {
        Usuario usuario = new Usuario( user.getDisplayName(), user.getEmail());

        Preferencias pref = Preferencias.getInstance();
        pref.inicializa(context);

        if (pref.usarFirestore()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("usuarios").document(user.getUid()).set(usuario);
        }
        else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference("usuarios/"+user.getUid()).setValue(usuario);
        }

    }

}
