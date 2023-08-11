package com.example.labproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InicioActivity extends AppCompatActivity {

    /*Conexion con BD*/
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@192.168.100.74:1521/XEPDB1"; //LUIS
    //private static final String URL = "jdbc:oracle:thin:@192.168.0.8:1521/XEPDB1"; //JENNY
    private static final String USERNAME = "ENCARGADO";
    private static final String PASSWORD = "ENCARGADO";

    /*VARIABLES USADAS*/
    TextView bienvenidoLabel, continuarLabel, nuevoUsuario;
    ImageView imageCompu;
    TextInputLayout usuarioTextField, contraseniaTextField;
    MaterialButton inicioSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        /*nombre de las variables Con su nuevo valor*/
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
                String usuario = usuarioTextField.getEditText().getText().toString();
                String contrasenia = contraseniaTextField.getEditText().getText().toString();

                // Llamar al método para realizar la validación en segundo plano
                validarUserPass(usuario, contrasenia);
            }
        });
    }
    private void validarUserPass(String usuario, String contrasenia){
        ValidarUserPassTask task = new ValidarUserPassTask();
        task.execute(usuario, contrasenia);
    }

    private class ValidarUserPassTask extends AsyncTask<String, Void, Boolean>{
        @Override
        protected Boolean doInBackground(String... strings) {
            String usuario = strings[0];
            String contrasenia = strings[1];
            try {
                // Cargar el controlador JDBC de Oracle
                Class.forName(DRIVER);

                // Establecer la conexión
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                // Preparar la consulta
                String sql = "SELECT COUNT(*) FROM ENCARGADO WHERE usuario = ? AND contrasenia = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, usuario);
                statement.setString(2, contrasenia);

                // Ejecutar la consulta
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                int count = resultSet.getInt(1);

                // Cerrar los recursos
                resultSet.close();
                statement.close();
                connection.close();

                return count > 0; // Si existe algún registro con las credenciales proporcionadas, se considera válido

            } catch (Exception e) {
                Log.e("Error", "Error en la consulta: " + e.toString());
            }

            return false; // En caso de algún error, se considera credenciales inválidas
        }

        protected void onPostExecute(Boolean result){
            if (result) {
                String usuario = usuarioTextField.getEditText().getText().toString();
                // Guardar el nombre de usuario en SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("usuario", usuario);
                editor.apply();
                // Credenciales válidas, procede a la siguiente actividad
                Intent intent = new Intent(InicioActivity.this, MenuPrincipalActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Credenciales inválidas, muestra un mensaje de error o realiza alguna acción
                Toast.makeText(InicioActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        }
    }
}