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

import com.example.labproject.reportes.ListaReporteAdapter;
import com.example.labproject.reportes.Reporte;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class FragmentoReportes extends Fragment implements SearchView.OnQueryTextListener {
    /*Conexion con BD*/
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    //private static final String URL = "jdbc:oracle:thin:@192.168.0.2:1521/XEPDB1"; //JENNY CASA WINDOWS
    //private static final String URL = "jdbc:oracle:thin:@192.168.0.12:1521/XEPDB1"; //JENNY CASA MAC
    private static final String URL = "jdbc:oracle:thin:@192.168.3.11:1521/XEPDB1"; //JENNY SERVICIO SOCIAL
    private static final String USERNAME = "ENCARGADO";
    private static final String PASSWORD = "ENCARGADO";
    SearchView textBuscarRep;
    ListaReporteAdapter adapter;
    RecyclerView listareportes;

    public FragmentoReportes() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmento_reportes, container, false);

        //referencia del buscar
        textBuscarRep = view.findViewById(R.id.textBuscarRep);
        textBuscarRep.setQueryHint("Búsqueda por nombre");

        // Cambiar color del texto y hint del SearchView
        EditText searchEditText = textBuscarRep.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(android.R.color.black)); // Cambia el color del texto
        searchEditText.setHintTextColor(getResources().getColor(android.R.color.darker_gray)); // Cambia el color del hint

        // Obtener referencia al RecyclerView desde la vista inflada del fragmento
        listareportes = view.findViewById(R.id.listareportes);
        listareportes.setLayoutManager(new LinearLayoutManager(getContext()));

        // Llamar al AsyncTask para realizar la consulta en segundo plano
        ConexionAsyncTask task = new ConexionAsyncTask();
        task.execute();

        textBuscarRep.setOnQueryTextListener(this);
        return view;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.filtradoR(s);
        return false;
    }

    private class ConexionAsyncTask extends AsyncTask<Void, Void, ArrayList<Reporte>> {

        @Override
        protected ArrayList<Reporte> doInBackground(Void... voids) {
            ArrayList<Reporte> listaReportes = new ArrayList<>();
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                // Cargar el controlador JDBC de Oracle
                Class.forName(DRIVER);
                // Establecer la conexiÃ³n a la base de datos
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                // Preparar la consulta SQL para seleccionar los encargados
                String sql = "SELECT\n" +
                        "    REPORTE.ID AS Numero_de_Reporte,\n" +
                        "    REPORTE.TITULO AS Titulo,\n" +
                        "    REPORTE.DESCRIPCION AS Descripcion,\n" +
                        "    TO_CHAR(REPORTE.HORA, 'HH24:MI') AS Hora,\n" +
                        "    TO_CHAR(REPORTE.FECHA, 'DD/MM/YYYY') AS Fecha,\n" +
                        "    REPORTE.SEGUIMIENTO AS Seguimiento,\n" +
                        "    ENCARGADO.NOMBRE AS Nombre,\n" +
                        "    ENCARGADO.APELLIDO_P AS ApellidoPat,\n" +
                        "    ENCARGADO.APELLIDO_M AS ApellidoMat,\n" +
                        "    COMPUTADORA.ID AS Compu\n" +
                        "FROM\n" +
                        "    REPORTE\n" +
                        "JOIN\n" +
                        "    ENCARGADO ON REPORTE.ENCARGADO_ID = ENCARGADO.ID\n" +
                        "LEFT JOIN\n" +
                        "    COMPUTADORA ON REPORTE.COMPUTADORA_ID = COMPUTADORA.ID";
                statement = connection.prepareStatement(sql);
                // Ejecutar la consulta
                resultSet = statement.executeQuery();
                // Recorrer el resultado y crear los objetos encargado
                while (resultSet.next()) {
                    Reporte reportitos = new Reporte();
                    reportitos.setID((resultSet.getInt("Numero_de_Reporte")));
                    reportitos.setTITULO(resultSet.getString("Titulo"));
                    String fecha = resultSet.getString("Fecha");
                    reportitos.setFECHA(fecha);
                    String hora = resultSet.getString("Hora");
                    reportitos.setHORA(hora);
                    listaReportes.add(reportitos);
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
                    Log.e("Error", "Error al cerrar la conexion: " + e.toString());
                }
            }
            return listaReportes;
        }

        protected void onPostExecute(ArrayList<Reporte> lista) {
            // Una vez terminada la consulta en segundo plano, actualizamos el RecyclerView con los datos
            adapter = new ListaReporteAdapter(lista);
            listareportes.setAdapter(adapter);
        }
    }
}