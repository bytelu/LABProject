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
import android.widget.EditText;

import com.example.labproject.profesor.Profesor;
import com.example.labproject.profesor.ProfesorAdapter;
import com.example.labproject.res.CData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


public class FragmentProfessors extends Fragment implements SearchView.OnQueryTextListener{

    /*Conexion con BD*/
    private static final String DRIVER = CData.getDriver();
    private static final String URL = CData.getUrl();
    private static final String USERNAME = CData.getUsername();
    private static final String PASSWORD = CData.getPassword();

    SearchView txtBuscar;
    ProfesorAdapter adapter;
    RecyclerView listaProfesores;

    public FragmentProfessors() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmento_profesores, container, false);

        txtBuscar = view.findViewById(R.id.txtBuscarProfesores);
        txtBuscar.setQueryHint("Busqueda por nombre");

        // Cambiar color del texto y hint del SearchView
        EditText searchEditText = txtBuscar.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(android.R.color.black)); // Cambia el color del texto
        searchEditText.setHintTextColor(getResources().getColor(android.R.color.darker_gray)); // Cambia el color del hint

        listaProfesores = view.findViewById(R.id.listProfesoresRecyclerView);
        listaProfesores.setLayoutManager(new LinearLayoutManager(getContext()));

        ConexionAsyncTask task = new ConexionAsyncTask();
        task.execute();

        txtBuscar.setOnQueryTextListener(this);

        return view;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String v) {
        adapter.filtradoP(v);
        return false;
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
                String sql = "SELECT\n" +
                        "   PROFESOR.NOMBRE AS Nombre, \n" +
                        "   PROFESOR.APELLIDO_P AS ApellidoP, \n" +
                        "   PROFESOR.APELLIDO_M AS ApellidoM, \n" +
                        "   PROFESOR.BOLETA AS Boleta\n" +
                        "   FROM PROFESOR";
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();
                while (resultSet.next()){
                    Profesor profesores = new Profesor();
                    profesores.setNOMBRE(resultSet.getString("Nombre"));
                    profesores.setAPELLIDO_P(resultSet.getString("ApellidoP"));
                    profesores.setAPELLIDO_M(resultSet.getString("ApellidoM"));
                    profesores.setBOLETA(resultSet.getString("Boleta"));
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