package com.example.labproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class RegistroActivity extends AppCompatActivity {

    TextView nuevoUsuario, bienvenidoLabel, continuarLabel;
    ImageView signUpImageView;
    TextInputLayout usuarioSignUpTextField, contraseniaTextField;
    MaterialButton inicioSesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        /*estos son los nombres que le puse al xml de Registro pueden cambiarse*/
        signUpImageView = findViewById(R.id.signUpImageView);
        bienvenidoLabel = findViewById(R.id.bienvenidoLabel);
        continuarLabel = findViewById(R.id.continuarLabel);
        usuarioSignUpTextField = findViewById(R.id.usuarioTextField);
        contraseniaTextField = findViewById(R.id.contraseniaTextField);
        inicioSesion = findViewById(R.id.inicioSesion);
        nuevoUsuario = findViewById(R.id.nuevoUsuario);

        nuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transicionRegreso();
            }
        });
    }

    @Override
    public void onBackPressed(){
        transicionRegreso();
    }

    public void transicionRegreso(){
        /*solo son animaciones para regresar al login activity*/
        Intent intent = new Intent(RegistroActivity.this, InicioActivity.class);
        Pair[] pairs = new Pair[7];
        pairs[0] = new Pair<View, String>(signUpImageView, "logoImageTrans");
        pairs[1] = new Pair<View, String>(bienvenidoLabel, "texTrans");
        pairs[2] = new Pair<View, String>(continuarLabel, "iniciaSesionTextTrans");
        pairs[3] = new Pair<View, String>(usuarioSignUpTextField, "usuarioInputTextTrans");
        pairs[4] = new Pair<View, String>(contraseniaTextField, "passwordInputTextTrans");
        pairs[5] = new Pair<View, String>(inicioSesion, "buttonSignUpTrans");
        pairs[6] = new Pair<View, String>(nuevoUsuario, "newUserTrans");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ActivityOptions options= ActivityOptions.makeSceneTransitionAnimation(RegistroActivity.this,pairs);
            startActivity(intent, options.toBundle());
        }else{
            /*aqui regresa al login activity*/
            startActivity(intent);
            finish();
        }
    }
}