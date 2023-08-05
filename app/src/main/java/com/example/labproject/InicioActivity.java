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

public class InicioActivity extends AppCompatActivity {
    TextView bienvenidoLabel, continuarLabel, nuevoUsuario;
    ImageView imageCompu;
    TextInputLayout usuarioTextField, contraseniaTextField;
    MaterialButton inicioSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        /*nombre de las variables*/
        imageCompu = findViewById(R.id.imageIconoCompu);
        bienvenidoLabel = findViewById(R.id.textViewBienvenido);
        continuarLabel = findViewById(R.id.textViewIniciarSesion);
        usuarioTextField = findViewById(R.id.usuarioTextField);
        contraseniaTextField = findViewById(R.id.contraseniaTextField);
        inicioSesion = findViewById(R.id.inicioSesion);
        nuevoUsuario = findViewById(R.id.nuevoUsuario);

        nuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*esto solo son animaciones*/
                Intent intent = new Intent(InicioActivity.this, RegistroActivity.class);
                Pair[] pairs = new Pair[7];
                pairs[0] = new Pair<View, String>(imageCompu, "logoImageTrans");
                pairs[1] = new Pair<View, String>(bienvenidoLabel, "texTrans");
                pairs[2] = new Pair<View, String>(continuarLabel, "iniciaSesionTextTrans");
                pairs[3] = new Pair<View, String>(usuarioTextField, "usuarioInputTextTrans");
                pairs[4] = new Pair<View, String>(contraseniaTextField, "passwordInputTextTrans");
                pairs[5] = new Pair<View, String>(inicioSesion, "buttonSignUpTrans");
                pairs[6] = new Pair<View, String>(nuevoUsuario, "newUserTrans");

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    /*solo comprueba si puede realizar la animacion*/
                    ActivityOptions options= ActivityOptions.makeSceneTransitionAnimation(InicioActivity.this,pairs);
                    startActivity(intent, options.toBundle());
                }else{
                    /*si esta correcto ingresa al nuevo activity que es el de registro  lineas de abajo*/
                    startActivity(intent);
                    finish();
                }
            }
        });

        inicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Aqui se debe validar de usuario y contraseña que ingresen en el login
                si es correcto realiza la lineas de codigo de abajo para ingresar al siguiente activity
                que es activity Principall*/
                Intent intent = new Intent(InicioActivity.this, MenuPrincipalActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}