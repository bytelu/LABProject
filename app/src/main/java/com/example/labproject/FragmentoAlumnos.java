package com.example.labproject;

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

import com.example.labproject.alumnos.Alumno;
import com.example.labproject.alumnos.ListaAlumnoAdapter;
import com.example.labproject.encargado.ListaEncargadosAdapte;
import com.example.labproject.encargado.encargado;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class FragmentoAlumnos extends Fragment implements SearchView.OnQueryTextListener {
    /*Conexion con BD*/
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@192.168.0.12:1521/XEPDB1"; //JENNY CASA
    //private static final String URL = "jdbc:oracle:thin:@192.168.100.30:1521/XEPDB1"; //JENNY SERVICIO SOCIAL
    private static final String USERNAME = "ENCARGADO";
    private static final String PASSWORD = "ENCARGADO";

    SearchView textBuscarAlum;
    ListaAlumnoAdapter adapter;
    RecyclerView listaalumnos;
    ArrayList<Alumno> listaArrayAlumnos;

    public FragmentoAlumnos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmento_alumnos, container, false);

        //referencia del buscar
        textBuscarAlum = view.findViewById(R.id.textBuscarAlum);
        textBuscarAlum.setQueryHint("Búsqueda por nombre");
        // Obtener referencia al RecyclerView desde la vista inflada del fragmento
        listaalumnos = view.findViewById(R.id.listaalumnos);
        listaalumnos.setLayoutManager(new LinearLayoutManager(getContext()));

        // Llamar al AsyncTask para realizar la consulta en segundo plano
        ConexionAsyncTask task = new ConexionAsyncTask();
        task.execute();

        textBuscarAlum.setOnQueryTextListener(this);

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

    private class ConexionAsyncTask extends AsyncTask<Void, Void, ArrayList<Alumno>> {

        @Override
        protected ArrayList<Alumno> doInBackground(Void... voids) {
            ArrayList<Alumno> listaAlumnos = new ArrayList<>();
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                // Cargar el controlador JDBC de Oracle
                Class.forName(DRIVER);
                // Establecer la conexión a la base de datos
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                // Preparar la consulta SQL para seleccionar los encargados
                String sql = "SELECT ESTUDIANTE.NOMBRE as Nombre, ESTUDIANTE.APELLIDO_P AS ApellidoPat, ESTUDIANTE.APELLIDO_M AS ApellidoMat, ESTUDIANTE.BOLETA as Boleta, CARRERA.CARRERA AS Carrera, ESTUDIANTE.SEMESTRE as Semestre\n" +
                        "FROM ESTUDIANTE\n" +
                        "JOIN CARRERA ON ESTUDIANTE.CARRERA_ID = CARRERA.ID";
                statement = connection.prepareStatement(sql);
                // Ejecutar la consulta
                resultSet = statement.executeQuery();
                // Recorrer el resultado y crear los objetos encargado
                while (resultSet.next()) {
                    Alumno alumnitos = new Alumno();
                    alumnitos.setNOMBRE(resultSet.getString("Nombre"));
                    alumnitos.setAPELLIDO_P(resultSet.getString("ApellidoPat"));
                    alumnitos.setAPELLIDO_M(resultSet.getString("ApellidoMat"));
                    alumnitos.setCARRERA(resultSet.getString("Carrera"));
                    alumnitos.setSEMESTRE(resultSet.getInt("Semestre"));
                    alumnitos.setBOLETA(resultSet.getInt("Boleta"));
                    listaAlumnos.add(alumnitos);
                }
            } catch (Exception e){
                Log.e("Error", "Error en la consulta: " + e.toString());
            }finally{
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
            return listaAlumnos;
        }

        protected void onPostExecute(ArrayList<Alumno> lista) {
            // Una vez terminada la consulta en segundo plano, actualizamos el RecyclerView con los datos
            adapter = new ListaAlumnoAdapter(lista);
            listaalumnos.setAdapter(adapter);
        }
    }
}