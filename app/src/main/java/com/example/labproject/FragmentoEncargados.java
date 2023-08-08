package com.example.labproject;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.labproject.encargado.ListaEncargadosAdapte;
import com.example.labproject.encargado.encargado;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FragmentoEncargados extends Fragment {
    /*Conexion con BD*/
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@192.168.100.74:1521/XEPDB1"; //LUIS
    //private static final String URL = "jdbc:oracle:thin:@192.168.3.11:1521/XEPDB1"; //SERVICIO SOCIAL
    private static final String USERNAME = "ENCARGADO";
    private static final String PASSWORD = "ENCARGADO";
    private Connection connection;
    private TextView textView;

    RecyclerView listaEncargados;
    ArrayList<encargado> listaArrayEncargados;

    public FragmentoEncargados() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmento_encargados, container, false);

        // Obtener referencia al RecyclerView desde la vista inflada del fragmento
        listaEncargados = view.findViewById(R.id.listaEncargados);
        listaEncargados.setLayoutManager(new LinearLayoutManager(getContext()));

        // Llamar al AsyncTask para realizar la consulta en segundo plano
        ConexionAsyncTask task = new ConexionAsyncTask();
        task.execute();

        return view;
    }

    private  class ConexionAsyncTask extends AsyncTask<Void, Void, ArrayList<encargado>>{

        @Override
        protected ArrayList<encargado> doInBackground(Void... voids) {
            ArrayList<encargado> listaEncargados = new ArrayList<>();
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                // Cargar el controlador JDBC de Oracle
                Class.forName(DRIVER);
                // Establecer la conexión a la base de datos
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                // Preparar la consulta SQL para seleccionar los encargados
                String sql = "SELECT nombre, apellido_p, apellido_m, TO_CHAR(hora_entrada, 'HH24:MI') AS hora_entrada, " +
                        "TO_CHAR(hora_salida, 'HH24:MI') AS hora_salida, usuario, estado " +
                        "FROM ENCARGADO";
                statement = connection.prepareStatement(sql);
                // Ejecutar la consulta
                resultSet = statement.executeQuery();
                // Recorrer el resultado y crear los objetos encargado
                while (resultSet.next()) {
                    encargado encargados = new encargado();
                    encargados = new encargado();
                    encargados.setNombre(resultSet.getString("nombre"));
                    encargados.setApellido_p(resultSet.getString("apellido_p"));
                    encargados.setApellido_m(resultSet.getString("apellido_m"));

                    // Obtener la hora en formato HH:mm
                    String horaEntrada = resultSet.getString("hora_entrada");
                    String horaSalida = resultSet.getString("hora_salida");
                    encargados.setHora_entrada(horaEntrada);
                    encargados.setHora_salida(horaSalida);

                    encargados.setUsuario(resultSet.getString("usuario"));
                    encargados.setEstado(resultSet.getInt("estado"));
                    listaEncargados.add(encargados);
                }
            }catch (Exception e){
                Log.e("Error", "Error en la consulta: " + e.toString());
            }finally {
                // Cerrar los recursos
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
                    Log.e("Error", "Error al cerrar la conexión: " + e.toString());
                }
            }
            return listaEncargados;
        }

        protected void onPostExecute(ArrayList<encargado> lista){
            // Una vez terminada la consulta en segundo plano, actualizamos el RecyclerView con los datos
            ListaEncargadosAdapte adapter = new ListaEncargadosAdapte(lista);
            listaEncargados.setAdapter(adapter);
        }
    }
}