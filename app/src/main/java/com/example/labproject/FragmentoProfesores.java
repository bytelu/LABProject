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

import com.example.labproject.entidades.Profesor;
import com.example.labproject.entidades.ProfesorAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class FragmentoProfesores extends Fragment {

    /*Conexion con BD*/
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@192.168.1.82:1521/XEPDB1";
    private static final String USERNAME = "ENCARGADO";
    private static final String PASSWORD = "ENCARGADO";
    private Connection connection;
    private RecyclerView listaProfesores;

    public FragmentoProfesores() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmento_profesores, container, false);
        listaProfesores = view.findViewById(R.id.listProfesoresRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        listaProfesores.setLayoutManager(layoutManager);

        ProfesorAdapter adapter = new ProfesorAdapter(new ArrayList<Profesor>(), getActivity());
        listaProfesores.setAdapter(adapter);

        new ConexionAsyncTask().execute();

        return view;
    }


    // AsyncTask para realizar la conexión y consulta en segundo plano
    private class ConexionAsyncTask extends AsyncTask<Void, Void, ArrayList<Profesor>> {
        @Override
        protected ArrayList<Profesor> doInBackground(Void... voids) {
            ArrayList<Profesor> ResultadoConsulta;
            try {
                ResultadoConsulta = mostrarProfesores();
            }catch (Exception e){
                // Manejo de errores en caso de problemas con la conexión o consulta
                ResultadoConsulta = new ArrayList<>();
                ResultadoConsulta.add(new Profesor("Error", "Error", "Error", "Error"));
            }
            return ResultadoConsulta;
        }

        @Override
        protected void onPostExecute(ArrayList<Profesor> resultadoConsulta) {
            ProfesorAdapter adapter = new ProfesorAdapter(resultadoConsulta, getContext());

            listaProfesores.setAdapter(adapter);
        }
    }

    public ArrayList<Profesor> mostrarProfesores(){
        ArrayList<Profesor> listaProfesores = new ArrayList<>();

        try {
            Class.forName(DRIVER);
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            Log.d("Conexion", "Conexión a la base de datos exitosa");


            String consulta = "SELECT NOMBRE, APELLIDO_P, APELLIDO_M, BOLETA FROM PROFESOR";
            PreparedStatement preparedStatement = connection.prepareStatement(consulta);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String nombre = resultSet.getString("NOMBRE");
                String apellidoP = resultSet.getString("APELLIDO_P");
                String apellidoM = resultSet.getString("APELLIDO_M");
                String boleta = resultSet.getString("BOLETA");

                Profesor profesores = new Profesor(nombre, apellidoP, apellidoM, boleta);
                listaProfesores.add(profesores);

            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        }
        catch (ClassNotFoundException | SQLException e){
            Log.e("Error", "Error en la consulta: " + e.toString());
        }

        return listaProfesores;

    }

}