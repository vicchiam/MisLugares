package com.example.mislugares.models;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by vicch on 11/02/2018.
 */

public class Usuarios {

    public static void guardarUsuario(final FirebaseUser user) {
        Usuario usuario = new Usuario( user.getDisplayName(), user.getEmail());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("usuarios/"+user.getUid()).setValue(usuario);
    }

}
