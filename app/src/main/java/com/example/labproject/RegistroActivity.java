package com.example.labproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.labproject.res.CData;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegistroActivity extends AppCompatActivity {

    private static final String DRIVER = CData.getDriver();
    private static final String URL = CData.getUrl();
    private static final String USERNAME = CData.getUsername();
    private static final String PASSWORD = CData.getPassword();

    TextView nuevoUsuario, bienvenidoLabel, continuarLabel;
    ImageView signUpImageView;
    TextInputLayout usuarioSignUpTextField, contraseniaTextField, confirmarcontrasenia,nombres, apMaterno, apPaterno;
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
        confirmarcontrasenia = findViewById(R.id.confirmarContraseniaTextField);
        inicioSesion = findViewById(R.id.inicioSesion);
        nuevoUsuario = findViewById(R.id.nuevoUsuario);
        apMaterno = findViewById(R.id.apMaternoTextField);
        apPaterno = findViewById(R.id.apPaternoTextField);
        nombres = findViewById(R.id.nombreTextField);

        nuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transicionRegreso();
            }
        });

        inicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String apellido_p = apPaterno.getEditText().getText().toString();
                String apellido_m = apMaterno.getEditText().getText().toString();
                String nombre = nombres.getEditText().getText().toString();
                String usuario = usuarioSignUpTextField.getEditText().getText().toString();
                String contrasenia = contraseniaTextField.getEditText().getText().toString();
                String repeatcontrasenia = confirmarcontrasenia.getEditText().getText().toString();

                // Validar que los campos no estén vacíos
                if (apellido_p.isEmpty() || apellido_m.isEmpty() || nombre.isEmpty() ||
                        usuario.isEmpty() || contrasenia.isEmpty() || repeatcontrasenia.isEmpty()) {
                    // Crear una variable para almacenar el mensaje de error
                    String mensajeError = "Por favor, completa los campos:\n";

                    // Verificar cada campo individualmente y agregarlo al mensaje de error si está vacío
                    if (apellido_p.isEmpty()) {
                        mensajeError += "- Apellido Paterno\n";
                    }
                    if (apellido_m.isEmpty()) {
                        mensajeError += "- Apellido Materno\n";
                    }
                    if (nombre.isEmpty()) {
                        mensajeError += "- Nombre\n";
                    }
                    if (usuario.isEmpty()) {
                        mensajeError += "- Usuario\n";
                    }
                    if (contrasenia.isEmpty()) {
                        mensajeError += "- Contraseña\n";
                    }
                    if (repeatcontrasenia.isEmpty()) {
                        mensajeError += "- Repetir Contraseña\n";
                    }
                    // Mostrar el mensaje de error con las credenciales faltantes
                    Toast.makeText(RegistroActivity.this, mensajeError, Toast.LENGTH_LONG).show();
                    return;
                }
                if (contrasenia.length() < 8 || repeatcontrasenia.length() < 8) {
                    contraseniaTextField.setError("La contraseña debe tener al menos 8 caracteres");
                    return;
                }

                // Validar que las contraseñas coincidan
                if (!contrasenia.equals(repeatcontrasenia)) {
                    // Mostrar un mensaje de error indicando que las contraseñas no coinciden
                    Toast.makeText(RegistroActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    // Limpiar los campos de contraseña y repetir contraseña
                    confirmarcontrasenia.getEditText().setText("");
                    contraseniaTextField.getEditText().setText("");
                    return;
                }

                // Llamar al método para realizar la validación en segundo plano
                registrarEncargado(apellido_p, apellido_m, nombre, usuario, contrasenia);

            }
        });
    }

    private void registrarEncargado(String apellido_p, String apellido_m, String nombre, String usuario, String contrasenia){
        RegistrarUserTask task = new RegistrarUserTask();
        task.execute(apellido_p, apellido_m, nombre, usuario, contrasenia);
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

    private class RegistrarUserTask extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            String apellido_p = strings[0];
            String apellido_m = strings[1];
            String nombre = strings[2];
            String usuario = strings[3];
            String contrasenia = strings[4];
            // Verificar si el usuario ya está registrado
            boolean usuarioRegistrado = verificarRegistros(usuario);

            // Si el usuario ya está registrado, devolvemos false
            if (usuarioRegistrado) {
                return false;
            } else {
                // Si el usuario no está registrado, procedemos a realizar el registro en la base de datos
                boolean registroExitoso = registrarUsuario(apellido_p, apellido_m, nombre, usuario, contrasenia);
                return registroExitoso;
            }
        }


        protected void onPostExecute(Boolean result){
            // Aquí puedes manejar el resultado del registro
            if (result) {
                // Registro exitoso, mostrar un mensaje o realizar alguna acción
                Toast.makeText(RegistroActivity.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegistroActivity.this, InicioActivity.class);
                startActivity(intent);
                // Finalizar la actividad actual (RegistroActivity)
                finish();
            } else {
                // Usuario ya registrado o error en el registro, mostrar un mensaje o realizar alguna acción
                Toast.makeText(RegistroActivity.this, "El usuario ya está registrado", Toast.LENGTH_SHORT).show();
                usuarioSignUpTextField.getEditText().setText("");
            }
        }
    }

    private boolean verificarRegistros(String Usuario){
        try {
            // Cargar el controlador JDBC de Oracle
            Class.forName(DRIVER);
            // Establecer la conexión a la base de datos
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // Preparar la consulta SQL SELECT
            String sql = "SELECT COUNT(*) FROM ENCARGADO WHERE usuario = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, Usuario);
            // Ejecutar la consulta
            ResultSet resultSet = statement.executeQuery();
            // Obtener el resultado de la consulta
            int count = 0;
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }

            // Cerrar los recursos
            resultSet.close();
            statement.close();
            connection.close();

            // Si count es mayor que 0, significa que el usuario ya está registrado
            return count > 0;

        } catch (Exception e) {
            Log.e("Error", "Error en la consulta: " + e.toString());
        }

        // En caso de algún error, consideramos que el usuario no está registrado
        return false;
    }

    private boolean registrarUsuario(String apellido_p, String apellido_m, String nombre, String usuario, String contrasenia){
        try {
            // Cargar el controlador JDBC de Oracle
            Class.forName(DRIVER);
            // Establecer la conexión a la base de datos
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // Preparar la consulta SQL INSERT
            String sql = "INSERT INTO ENCARGADO (nombre, apellido_p, apellido_m, usuario, contrasenia) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, nombre);
            statement.setString(2, apellido_p);
            statement.setString(3, apellido_m);
            statement.setString(4, usuario);
            statement.setString(5, contrasenia);

            // Ejecutar la consulta de inserción
            int rowsInserted = statement.executeUpdate();
            // Cerrar los recursos
            statement.close();
            connection.close();
            // Si rowsInserted es mayor que 0, significa que se insertó el registro exitosamente
            return rowsInserted > 0;

        } catch (Exception e) {
            Log.e("Error", "Error en la consulta: " + e.toString());
        }

        // En caso de algún error, consideramos que el registro no fue exitoso
        return false;
    }
}