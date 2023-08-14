
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

import com.example.labproject.sesiongrupallab1.ListaSesionGrupalLabUnoAdapter;
import com.example.labproject.sesiongrupallab1.SesionGrupalLaboratorioUno;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


public class FragmentoLabUno extends Fragment implements SearchView.OnQueryTextListener {

    /*Conexion con BD*/
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";

    //private static final String URL = "jdbc:oracle:thin:@192.168.100.74:1521/XEPDB1"; //LUIS
    private static final String URL = "jdbc:oracle:thin:@192.168.3.11:1521/XEPDB1"; //SERVICIO SOCIAL
    private static final String USERNAME = "ENCARGADO";
    private static final String PASSWORD = "ENCARGADO";

    SearchView txtBuscar;
    ListaSesionGrupalLabUnoAdapter adapter;
    RecyclerView listaLaboratorioUnoGrupal;

    public FragmentoLabUno() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragmento_lab_uno, container, false);

        //referencia del buscar
        txtBuscar = view.findViewById(R.id.txtBuscar);
        txtBuscar.setQueryHint("Búsqueda");

        // Cambiar color del texto y hint del SearchView
        EditText searchEditText = txtBuscar.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(android.R.color.black)); // Cambia el color del texto
        searchEditText.setHintTextColor(getResources().getColor(android.R.color.darker_gray)); // Cambia el color del hint

        // Obtener referencia al RecyclerView desde la vista inflada del fragmento
        listaLaboratorioUnoGrupal = view.findViewById(R.id.listaLaboratorioUnoGrupal);
        listaLaboratorioUnoGrupal.setLayoutManager(new LinearLayoutManager(getContext()));

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

    private class ConexionAsyncTask extends AsyncTask<Void, Void, ArrayList<SesionGrupalLaboratorioUno>>{

        @Override
        protected ArrayList<SesionGrupalLaboratorioUno> doInBackground(Void... voids) {
            ArrayList<SesionGrupalLaboratorioUno> listaSesionGrupalLabUno = new ArrayList<>();
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                //PREPARAR LA CONSULTA SQL PARA SELECCIONAR LAS SESIONES GRUPALES DEL LAB 1
                String sql = "SELECT\n" +
                        "    TO_CHAR(sesion.fecha, 'DD-MM-YYYY') AS fecha,\n" +
                        "    TO_CHAR(sesion.hora_inicio, 'HH24:MI') AS hora_inicio,\n" +
                        "    TO_CHAR(sesion.hora_final, 'HH24:MI') AS hora_final,\n" +
                        "    encargado.nombre AS nombre_encargado,\n" +
                        "    encargado.apellido_p AS encargado_apellido_p,\n" +
                        "    encargado.apellido_m AS encargado_apellido_m,\n" +
                        "    computadora.numero AS numero_computadora,\n" +
                        "    computadora.laboratorio as laboratorio_computadora,\n" +
                        "    estudiante.nombre AS nombre_alumno,\n" +
                        "    estudiante.apellido_p AS alumno_apellido_p,\n" +
                        "    estudiante.apellido_m AS alumno_apellido_m,\n" +
                        "    estudiante.boleta AS no_boleta,\n" +
                        "    estudiante.semestre AS semestre_alumno,\n" +
                        "    profesor.nombre AS nombre_profesor,\n" +
                        "    profesor.apellido_p AS profesor_apellido_p,\n" +
                        "    profesor.apellido_m AS profesor_apellido_m\n" +
                        "FROM sesion\n" +
                        "JOIN encargado ON sesion.encargado_id = encargado.id\n" +
                        "JOIN estudiante ON sesion.estudiante_id = estudiante.id\n" +
                        "JOIN computadora ON sesion.computadora_id = computadora.id\n" +
                        "JOIN profesor ON sesion.profesor_id = profesor.id\n" +
                        "WHERE sesion.activo = 1\n" +
                        "  AND computadora.laboratorio = 1";
                statement = connection.prepareStatement(sql);
                //ejecutar consulta
                resultSet = statement.executeQuery();
                // Recorrer el resultado y crear los objetos de los alumnos
                while (resultSet.next()){
                    SesionGrupalLaboratorioUno sesGruLabUno = new SesionGrupalLaboratorioUno();
                    sesGruLabUno.setComputadora(Integer.toString(resultSet.getInt("numero_computadora")));
                    sesGruLabUno.setNoBoleta(Integer.toString(resultSet.getInt("no_boleta")));// Convertir int a String
                    sesGruLabUno.setAluNombre(resultSet.getString("nombre_alumno"));
                    sesGruLabUno.setAluApeP(resultSet.getString("alumno_apellido_p"));
                    sesGruLabUno.setAluApeM(resultSet.getString("alumno_apellido_m"));
                    sesGruLabUno.setSemestreAlu(Integer.toString(resultSet.getInt("semestre_alumno")));

                    listaSesionGrupalLabUno.add(sesGruLabUno);
                }
            }catch (Exception e){
                Log.e("Error", "Error en la consulta: " + e.toString());
            }finally {
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
                }catch (Exception e){
                    Log.e("Error", "Error al cerrar la conexión: " + e.toString());
                }
            }
            return listaSesionGrupalLabUno;
        }

        protected void onPostExecute(ArrayList<SesionGrupalLaboratorioUno> lista){
            adapter = new ListaSesionGrupalLabUnoAdapter(lista);
            listaLaboratorioUnoGrupal.setAdapter(adapter);
        }
    }
}