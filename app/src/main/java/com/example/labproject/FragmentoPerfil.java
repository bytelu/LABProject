package com.example.labproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.labproject.perfil.Perfil;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FragmentoPerfil extends Fragment {

    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@192.168.1.13:1521/XEPDB1";
    private static final String USERNAME = "ENCARGADO";
    private static final String PASSWORD = "ENCARGADO";
    private TextView nombreTextView, usuarioTextView, horaEntradaTextView, horaSalidaTextView;
    private TextInputLayout UsuarioTextEdit, ContrasenaTextEdit;

    public FragmentoPerfil() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragmento_perfil, container, false);
        nombreTextView = view.findViewById(R.id.nombreTextView);
        usuarioTextView = view.findViewById(R.id.usuarioTextView);
        horaEntradaTextView = view.findViewById(R.id.horaEntradaTextView);
        horaSalidaTextView = view.findViewById(R.id.horaSalidaTextView);
        UsuarioTextEdit = view.findViewById(R.id.UsuarioTextInput);
        ContrasenaTextEdit = view.findViewById(R.id.ContrasenaTextInput);

        Button button = view.findViewById(R.id.saveChangesButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nuevoUsuario = UsuarioTextEdit.getEditText().getText().toString();
                String nuevaContrasena = ContrasenaTextEdit.getEditText().getText().toString();

                UpdateAsyncTask updateTask = new UpdateAsyncTask();
                updateTask.execute(nuevoUsuario, nuevaContrasena);
            }
        });

        ConexionAsyncTask task = new ConexionAsyncTask();
        task.execute();

        return view;
    }

    private class ConexionAsyncTask extends AsyncTask<Void, Void, Perfil>{

        @Override
        protected Perfil doInBackground(Void... voids) {
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            Perfil perfil = null;
            try {
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                String usuario = sharedPreferences.getString("usuario", "");

                String sql = "SELECT NOMBRE, APELLIDO_P, APELLIDO_M, HORA_ENTRADA, HORA_SALIDA, USUARIO, CONTRASENIA FROM ENCARGADO WHERE USUARIO = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, usuario);
                resultSet = statement.executeQuery();
                if (resultSet.next()){
                    perfil = new Perfil();
                    perfil.setName(resultSet.getString("NOMBRE"), resultSet.getString("APELLIDO_M"), resultSet.getString("APELLIDO_P"));
                    perfil.setHora_entrada(resultSet.getString("HORA_ENTRADA"));
                    perfil.setHora_salida(resultSet.getString("HORA_SALIDA"));
                    perfil.setUsuario(resultSet.getString("USUARIO"));
                    perfil.setContrasenia(resultSet.getString("CONTRASENIA"));
                } else {
                    Log.e("Error", "No se encontraron resultados en la consulta.");
                }
            } catch (Exception e) {
                Log.e("Error", "Error en la consulta" + e);
            } finally {
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception e) {
                    Log.e("Error", "Error en la consulta" + e);
                }
            }
            return perfil;
        }

        @Override
        protected void onPostExecute(Perfil perfil) {
            if (perfil != null) {
                nombreTextView.setText(perfil.getName());
                usuarioTextView.setText(perfil.getUsuario());
                horaEntradaTextView.setText(perfil.getHora_entrada());
                horaSalidaTextView.setText(perfil.getHora_salida());
                UsuarioTextEdit.getEditText().setText(perfil.getUsuario());
                ContrasenaTextEdit.getEditText().setText(perfil.getContrasenia());
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-M-d.H.m.s");
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");

                String horaEntradaString = perfil.getHora_entrada();
                try {
                    Date horaEntradaDate = inputFormat.parse(horaEntradaString);
                    String horaEntradaFormateada = outputFormat.format(horaEntradaDate);
                    horaEntradaTextView.setText(horaEntradaFormateada);
                } catch (Exception e) {
                    Log.e("Error", "Error al formatear la hora de entrada: " + e);
                }

                String horaSalidaString = perfil.getHora_salida();
                try {
                    Date horaSalidaDate = inputFormat.parse(horaSalidaString);
                    String horaSalidaFormateada = outputFormat.format(horaSalidaDate);
                    horaSalidaTextView.setText(horaSalidaFormateada);
                } catch (Exception e) {
                    Log.e("Error", "Error al formatear la hora de salida: " + e);
                }
            } else {
                Toast.makeText(requireContext(), "Error al cargar el perfil", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdateAsyncTask extends AsyncTask<String, Void, Boolean>{
        @Override
        protected Boolean doInBackground(String... params){
            Connection connection = null;
            PreparedStatement statement = null;

            try {
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                String nuevoUsuario = params[0];
                String nuevaContrasena = params[1];
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                String usuarioActual = sharedPreferences.getString("usuario", "");

                String sql = "UPDATE ENCARGADO SET USUARIO = ?, CONTRASENIA = ? WHERE USUARIO = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, nuevoUsuario);
                statement.setString(2, nuevaContrasena);
                statement.setString(3, usuarioActual);

                int rowsAffected = statement.executeUpdate();

                return rowsAffected > 0;
            } catch (Exception e){
                Log.e("Error", "Error en la actualización" + e);
                return false;
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception e) {
                    Log.e("Error", "Error en la actualización" + e);
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success){
            if (success) {
                Toast.makeText(requireContext(), "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Error al guardar los cambios", Toast.LENGTH_SHORT).show();
            }
        }
    }
}