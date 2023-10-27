package com.example.labproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.labproject.res.CData;
import com.example.labproject.sesindividual.ListaSesionIndividualAdapter;
import com.example.labproject.sesindividual.SesionIndividual;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FragmentoSesionIndividual extends Fragment implements SearchView.OnQueryTextListener{
    /*Conexion con BD*/
    private static final String DRIVER = CData.getDriver();
    private static final String URL = CData.getUrl();
    private static final String USERNAME = CData.getUsername();
    private static final String PASSWORD = CData.getPassword();

    SearchView txtBuscar;
    ListaSesionIndividualAdapter adapter;
    RecyclerView listaSesionIndividual;
    ArrayList<SesionIndividual> listaArraySesionIndividual;
    public FragmentoSesionIndividual() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragmento_sesion_individual, container, false);

        //referencia del buscar
        txtBuscar = view.findViewById(R.id.txtBuscar);
        txtBuscar.setQueryHint("Búsqueda");

        // Cambiar color del texto y hint del SearchView
        EditText searchEditText = txtBuscar.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(android.R.color.black)); // Cambia el color del texto
        searchEditText.setHintTextColor(getResources().getColor(android.R.color.darker_gray)); // Cambia el color del hint

        // Obtener referencia al RecyclerView desde la vista inflada del fragmento
        listaSesionIndividual = view.findViewById(R.id.listaSesIndividual);
        listaSesionIndividual.setLayoutManager(new LinearLayoutManager(getContext()));

        // Llamar al AsyncTask para realizar la consulta en segundo plano
        ConexionAsyncTask task = new ConexionAsyncTask();
        task.execute();

        txtBuscar.setOnQueryTextListener(this);

        return view;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filtrado(newText);
        return false;
    }

    private  class ConexionAsyncTask extends AsyncTask<Void, Void, ArrayList<SesionIndividual>>{
        protected ArrayList<SesionIndividual> doInBackground(Void... voids){
            ArrayList<SesionIndividual> listaSesionIndividual = new ArrayList<>();
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                // Cargar el controlador JDBC de Oracle
                Class.forName(DRIVER);
                // Establecer la conexión a la base de datos
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                // Preparar la consulta SQL para seleccionar las sesiones individuales
                String sql = "SELECT sesion.id AS idSesion,\n" +
                        "    DATE_FORMAT(fecha, '%d-%m-%Y') AS fecha,\n" +
                        "    DATE_FORMAT(sesion.hora_inicio, '%H:%i') AS hora_inicio,\n" +
                        "    DATE_FORMAT(sesion.hora_final, '%H:%i') AS hora_final,\n" +
                        "    encargado.nombre AS nombre_encargado,\n" +
                        "    encargado.apellido_p AS encargado_apellido_p,\n" +
                        "    encargado.apellido_m AS encargado_apellido_m,\n" +
                        "    computadora.numero AS numero_computadora,\n" +
                        "    computadora.laboratorio as laboratorio_computadora,\n" +
                        "    estudiante.nombre AS nombre_alumno,\n" +
                        "    estudiante.apellido_p AS alumno_apellido_p,\n" +
                        "    estudiante.apellido_m AS alumno_apellido_m\n" +
                        "FROM sesion\n" +
                        "JOIN encargado ON sesion.encargado_id = encargado.id\n" +
                        "JOIN estudiante ON sesion.estudiante_id = estudiante.id\n" +
                        "JOIN computadora ON sesion.computadora_id = computadora.id\n" +
                        "LEFT JOIN profesor ON sesion.profesor_id = profesor.id\n" +
                        "WHERE sesion.activo = 1\n" +
                        "  AND sesion.profesor_id IS NULL";
                statement = connection.prepareStatement(sql);
                // Ejecutar la consulta
                resultSet = statement.executeQuery();
                // Recorrer el resultado y crear los objetos de los alumnos
                while (resultSet.next()) {
                    SesionIndividual sesionIndv = new SesionIndividual();
                    String fecha = resultSet.getString("fecha");
                    String fechaFormateada = fecha.substring(0, 10); // Obtener los primeros 10 caracteres (YYYY-MM-DD)
                    sesionIndv.setFecha(fechaFormateada);
                    sesionIndv.setComputadora(Integer.toString(resultSet.getInt("numero_computadora")));
                    sesionIndv.setLaboratorio(Integer.toString(resultSet.getInt("laboratorio_computadora")));// Convertir int a String
                    sesionIndv.setAluNombre(resultSet.getString("nombre_alumno"));
                    sesionIndv.setAluApeP(resultSet.getString("alumno_apellido_p"));
                    sesionIndv.setAluApeM(resultSet.getString("alumno_apellido_m"));
                    sesionIndv.setEncNombre(resultSet.getString("nombre_encargado"));
                    sesionIndv.setEncApeP(resultSet.getString("encargado_apellido_p"));
                    sesionIndv.setEncApeM(resultSet.getString("encargado_apellido_m"));
                    sesionIndv.setIdSesion(resultSet.getString("idSesion"));
                    // Obtener la hora en formato HH:mm
                    String horaEntrada = resultSet.getString("hora_inicio");
                    //String horaSalida = resultSet.getString("hora_final");
                    sesionIndv.setSesEntrada(horaEntrada);

                    listaSesionIndividual.add(sesionIndv);
                }
            }catch (Exception e){
                Log.e("Error", "Error en la consulta: " + e);
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
                    Log.e("Error", "Error al cerrar la conexión: " + e);
                }
            }
            return listaSesionIndividual;
        }

        protected void onPostExecute(ArrayList<SesionIndividual> lista){
            // Una vez terminada la consulta en segundo plano, actualizamos el RecyclerView con los datos
            adapter = new ListaSesionIndividualAdapter(lista);
            listaSesionIndividual.setAdapter(adapter);
        }
    }
    public class ActualizarSesionAsyncTask extends AsyncTask<String, Void, Boolean>{
        @Override
        protected Boolean doInBackground(String... strings) {
            String idSesion = strings[0];
            Connection connection = null;
            PreparedStatement preparedStatement = null;

            try {
                // Cargar el controlador JDBC de Oracle
                Class.forName(DRIVER);
                // Establecer la conexión a la base de datos
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                String updateQuery = "UPDATE sesion AS s " +
                        "INNER JOIN computadora AS c ON s.computadora_id = c.id " +
                        "SET s.activo = 0, s.hora_final = ?, c.ocupada = 0 " +
                        "WHERE s.id = ?";
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String horaActual = timeFormat.format(new Date());
                preparedStatement = connection.prepareStatement(updateQuery);
                preparedStatement.setString(1, horaActual);
                preparedStatement.setString(2, idSesion);

                int rowsAffected = preparedStatement.executeUpdate();

                // Cierra los recursos en un bloque finally
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }

                Log.e("Sesion Individual Finalizada", "Sesión finalizada id " + idSesion);

                return rowsAffected > 0;

            } catch (Exception e) {
                Log.e("Error", "Error al finalizar sesion: " + e.toString());
                return false;
            }finally {
                // Asegurarse de cerrar los recursos en caso de excepción
                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception e) {
                    Log.e("Error", "Error al cerrar los recursos: " + e.toString());
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Log.e("Sesion Individual Finalizada", "Se logro" + success);

            } else {
                Log.e("Sesion Individual Finalizada", "Tambien se logro" + success);
            }
        }
    }
}