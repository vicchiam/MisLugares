package com.example.mislugares.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.mislugares.R;
import com.example.mislugares.utilities.VolleySingleton;
import com.example.mislugares.firebase.CustomLoginActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by vicch on 08/02/2018.
 */

public class UsuarioFragment extends Fragment {

    private View vista;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor, Bundle savedInstanceState) {
        vista = inflador.inflate(R.layout.fragment_usuario, contenedor, false);
        context=this.getContext();

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        TextView nombre = (TextView) vista.findViewById(R.id.nombre);
        nombre.setText(usuario.getDisplayName());

        TextView correo = (TextView) vista.findViewById(R.id.correo);
        correo.setText(usuario.getEmail());

        TextView proveedor = (TextView) vista.findViewById(R.id.proveedor);
        proveedor.setText(usuario.getProviders().toString());

        TextView tel = (TextView) vista.findViewById(R.id.telefono);
        tel.setText(usuario.getPhoneNumber());

        TextView uid = (TextView) vista.findViewById(R.id.uid);
        uid.setText(usuario.getUid());

        Button cerrarSesion =(Button) vista.findViewById(R.id.btn_cerrar_sesion);
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AuthUI.getInstance()
                        .signOut(getActivity())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent i = new Intent(getActivity(), CustomLoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                getActivity().finish();
                            }
                        });
            }
        });


        //Cargar foto
        Uri urlImagen = usuario.getPhotoUrl();
        if(urlImagen != null){
            NetworkImageView fotoUsuario = (NetworkImageView) vista.findViewById(R.id.imagen);
            fotoUsuario.setImageUrl(urlImagen.toString(), VolleySingleton.getLectorImagenes(this.getContext()));
        }

        //Anonimo
        Button unirCuenta = (Button) vista.findViewById(R.id.btn_unificar);
        unirCuenta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),CustomLoginActivity.class);
                i.putExtra("unificar",true);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                getActivity().finish();
            }
        });

        if(usuario.getProviders().get(0).equals("password")) {

            ImageView editarFoto = (ImageView) vista.findViewById(R.id.editarFoto);
            editarFoto.setVisibility(View.VISIBLE);
            editarFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mostrarEditarFoto();
                }
            });

            ImageView editarNombre = (ImageView) vista.findViewById(R.id.editarNombre);
            editarNombre.setVisibility(View.VISIBLE);
            editarNombre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mostrarEditarNombre();
                }
            });

            ImageView editarPassword = (ImageView) vista.findViewById(R.id.editarPassword);
            editarPassword.setVisibility(View.VISIBLE);
            editarPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mostrarEditarPassword();
                }
            });

        }

        return vista;
    }

    public void mostrarEditarFoto(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Indica una url de imagen");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Modificar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String text=input.getText().toString();
                dialogInterface.dismiss();
                ponerFoto(text);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void mostrarEditarNombre(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Indica el nuevo nombre");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Modificar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String text=input.getText().toString();
                dialogInterface.dismiss();
                ponerNombre(text);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void mostrarEditarPassword(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Modificar la contraseña");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("Indica la contraseña actual");

        final EditText input2 = new EditText(getContext());
        input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input2.setHint("Indica la nueva contraseña");

        final EditText input3 = new EditText(getContext());
        input3.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input3.setHint("Repite la contraseña");

        final LinearLayout layout=new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(input);
        layout.addView(input2);
        layout.addView(input3);

        builder.setView(layout);

        builder.setPositiveButton("Modificar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String oldPass=input.getText().toString();
                String pass1=input2.getText().toString();
                String pass2=input2.getText().toString();
                dialogInterface.dismiss();
                ponerPassword(oldPass, pass1, pass2, false);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void mostrarMensaje(String msj){
        Snackbar.make(vista,msj,Snackbar.LENGTH_LONG).show();
    }

    private void ponerFoto(String url){
        if(url.length()==0){
            mostrarMensaje("No puede ser un campo vacio");
            return;
        }
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest perfil = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(url)).build();
        usuario.updateProfile(perfil)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
                        //Cargar foto
                        Uri urlImagen = usuario.getPhotoUrl();
                        if(urlImagen != null){
                            NetworkImageView fotoUsuario = (NetworkImageView) vista.findViewById(R.id.imagen);
                            fotoUsuario.setImageUrl(urlImagen.toString(), VolleySingleton.getLectorImagenes(context));
                        }

                        mostrarMensaje("Foto actualizada");
                    }
                });
    }

    private void ponerNombre(String nombre){
        if(nombre.length()==0){
            mostrarMensaje("No puede ser un campo vacio");
            return;
        }
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest perfil = new UserProfileChangeRequest.Builder()
                .setDisplayName(nombre).build();
        usuario.updateProfile(perfil)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
                        String nombre=usuario.getDisplayName();
                        if(nombre!=null){
                            TextView displayName = (TextView) vista.findViewById(R.id.nombre);
                            displayName.setText(usuario.getDisplayName());
                        }

                        mostrarMensaje("Nombre actualizado");
                    }
                });
    }

    private void ponerPassword(final String oldPass, final String pass1, final String pass2, final boolean intentado){
        if(pass1.length()==0 || pass2.length()==0){
            mostrarMensaje("No puedes dejar un campo vacio");
        }
        else if(!pass1.equals(pass2)){
            mostrarMensaje("Las contraseñas no son iguales");
        }
        else{
            final FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
            usuario.updatePassword(pass1)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mostrarMensaje("Contraseña modificada");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(e instanceof FirebaseAuthRecentLoginRequiredException){
                                if(!intentado){
                                    reatenticar(usuario.getEmail(), oldPass, pass1, pass2);
                                }
                            }
                        }
                    });
        }
    }

    private void reatenticar(final String mail, final String oldPass, final String pass1, final String pass2){

        AuthCredential credential = EmailAuthProvider.getCredential(mail, oldPass);
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        usuario.reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ponerPassword(oldPass, pass1, pass2, true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mostrarMensaje("No se ha podido modificar la contraseña");
            }
        });
    }



}
