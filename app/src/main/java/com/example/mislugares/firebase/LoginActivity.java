package com.example.mislugares.firebase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.example.mislugares.R;
import com.example.mislugares.activities.MainActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

/**
 * Created by vicch on 06/02/2018.
 */

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login();
    }

    private void login() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) {
            usuario.reload();
            Log.e("Usuario", usuario.getEmail()+" "+usuario.getUid()+" - "+usuario.getProviders().get(0));
            if(usuario.getProviders().get(0).equals("password")){
                if(usuario.isEmailVerified()){
                    Toast.makeText(this, "inicia sesión: " + usuario.getDisplayName()+" - "+ usuario.getEmail()+" - "+ usuario.getProviders().get(0),Toast.LENGTH_LONG).show();
                    irActividadPrincipal();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Advertencia")
                            .setMessage("Es necesario verificar la cuenta, revisa tu correo")
                            .setPositiveButton("Volver a enviar", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                    login();
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("Ya lo he verificado", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    login();
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNeutralButton("Salir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .show();
                }
            }
            else if(usuario.getProviders().get(0).equals("google.com")){
                irActividadPrincipal();
            }
        } else {
            mostrarViewLogin();
        }
    }

    private void irActividadPrincipal(){
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void mostrarViewLogin(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                                )
                        )
                        .setIsSmartLockEnabled(false)
                        .build(), RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == ResultCodes.OK) {
                login();
            }
            else {
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (response == null) {
                    Toast.makeText(this,"Cancelado",Toast.LENGTH_LONG).show();
                    return;
                }
                else if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this,"Sin conexión a Internet", Toast.LENGTH_LONG).show();
                    return;
                }
                else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this,"Error desconocido", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

}
