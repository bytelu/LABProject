package com.example.labproject;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.labproject.profesor.Profesor;
import com.example.labproject.profesor.ProfesorAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


public class FragmentProfessors extends Fragment {

    /*Conexion con BD*/
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@192.168.1.73:1521/XEPDB1";
    private static final String USERNAME = "ENCARGADO";
    private static final String PASSWORD = "ENCARGADO";
    ProfesorAdapter adapter;
    RecyclerView listaProfesores;
    ArrayList<Profesor> listaArrayProfesor;

    public FragmentProfessors() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmento_profesores, container, false);

        listaProfesores = view.findViewById(R.id.listProfesoresRecyclerView);
        listaProfesores.setLayoutManager(new LinearLayoutManager(getContext()));

        ConexionAsyncTask task = new ConexionAsyncTask();
        task.execute();

        return view;
    }


    // AsyncTask para realizar la conexión y consulta en segundo plano
    private class ConexionAsyncTask extends AsyncTask<Void, Void, ArrayList<Profesor>> {
        @Override
        protected ArrayList<Profesor> doInBackground(Void... voids) {
            ArrayList<Profesor> listaProfesores = new ArrayList<>();
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                String sql = "SELECT NOMBRE, APELLIDO_P, APELLIDO_M, BOLETA FROM PROFESOR";
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();
                while (resultSet.next()){
                    Profesor profesores = new Profesor();
                    profesores.setNOMBRE(resultSet.getString("NOMBRE"));
                    profesores.setAPELLIDO_P(resultSet.getString("APELLIDO_P"));
                    profesores.setAPELLIDO_M(resultSet.getString("APELLIDO_M"));
                    profesores.setBOLETA(resultSet.getString("BOLETA"));
                    listaProfesores.add(profesores);
                }
            }catch (Exception e){
                Log.e("Error", "Error en la consulta" + e);
            }finally {
                try {
                    if (resultSet != null){
                        resultSet.close();
                    }
                    if (statement != null){
                        statement.close();
                    }
                    if (connection != null){
                        connection.close();
                    }
                } catch (Exception e) {
                    Log.e("Error", "Error en la consulta" + e);
                }
            }
            return listaProfesores;
        }

        @Override
        protected void onPostExecute(ArrayList<Profesor> lista) {
            adapter = new ProfesorAdapter(lista);
            listaProfesores.setAdapter(adapter);
        }
    }
}