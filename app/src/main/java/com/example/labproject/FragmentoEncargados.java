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

import com.example.labproject.encargado.ListaEncargadosAdapte;
import com.example.labproject.encargado.encargado;
import com.example.labproject.res.CData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class FragmentoEncargados extends Fragment implements SearchView.OnQueryTextListener{
    /*Conexion con BD*/
    private static final String DRIVER = CData.getDriver();
    private static final String URL = CData.getUrl();
    private static final String USERNAME = CData.getUsername();
    private static final String PASSWORD = CData.getPassword();

    SearchView txtBuscar;
    ListaEncargadosAdapte adapter;
    RecyclerView listaEncargados;

    public FragmentoEncargados() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmento_encargados, container, false);

        //referencia del buscar
        txtBuscar = view.findViewById(R.id.txtBuscar);
        txtBuscar.setQueryHint("Búsqueda por nombre");

        // Cambiar color del texto y hint del SearchView
        EditText searchEditText = txtBuscar.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(android.R.color.black)); // Cambia el color del texto
        searchEditText.setHintTextColor(getResources().getColor(android.R.color.darker_gray)); // Cambia el color del hint

        // Obtener referencia al RecyclerView desde la vista inflada del fragmento
        listaEncargados = view.findViewById(R.id.listaEncargados);
        listaEncargados.setLayoutManager(new LinearLayoutManager(getContext()));

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
                String sql = "SELECT nombre, apellido_p, apellido_m, hora_entrada, " +
                        "hora_salida, usuario, estado " +
                        "FROM ENCARGADO";
                statement = connection.prepareStatement(sql);
                // Ejecutar la consulta
                resultSet = statement.executeQuery();
                // Recorrer el resultado y crear los objetos encargado
                while (resultSet.next()) {
                    encargado encargados = new encargado();
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
            return listaEncargados;
        }

        protected void onPostExecute(ArrayList<encargado> lista){
            // Una vez terminada la consulta en segundo plano, actualizamos el RecyclerView con los datos
            adapter = new ListaEncargadosAdapte(lista);
            listaEncargados.setAdapter(adapter);
        }
    }
}