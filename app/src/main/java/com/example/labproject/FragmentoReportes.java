package com.example.labproject;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.labproject.alumnos.Alumno;
import com.example.labproject.alumnos.ListaAlumnoAdapter;
import com.example.labproject.encargado.ListaEncargadosAdapte;
import com.example.labproject.encargado.encargado;
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
    private static final String URL = "jdbc:oracle:thin:@192.168.0.2:1521/XEPDB1"; //JENNY CASA WINDOWS
    //private static final String URL = "jdbc:oracle:thin:@192.168.0.12:1521/XEPDB1"; //JENNY CASA MAC
    //private static final String URL = "jdbc:oracle:thin:@192.168.100.30:1521/XEPDB1"; //JENNY SERVICIO SOCIAL
    private static final String USERNAME = "ENCARGADO";
    private static final String PASSWORD = "ENCARGADO";

    private TextView NumRep;
    private TextView FechaRep;
    private TextView HoraRep;
    private TextView textView4;
    private TextView NombreEncRep;
    private TextView ApePEncRep;
    private TextView ApeMEncRep;
    private TextView NumCompu;
    private TextView TitleRep;
    private TextView DescRep;
    private Button AgregarRep;
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filtradoR(newText);
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
                // Establecer la conexión a la base de datos
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                // Preparar la consulta SQL para seleccionar los encargados
                String sql = "SELECT REPORTE.ID as Numero de Reporte,REPORTE.TITULO AS Titulo, REPORTE.DESCRIPCION as Descripcion,TO_CHAR(REPORTE.HORA, 'HH24:MI') AS Hora, TO_CHAR(REPORTE.FECHA, 'DD/MM/YYYY') AS Fecha, \n" +
                        "ENCARGADO.NOMBRE as Nombre, ENCARGADO.APELLIDO_P as ApellidoPat,ENCARGADO.APELLIDO_M as ApellidoMat, COMPUTADORA.ID AS Compu\n" +
                        "FROM REPORTE\n" +
                        "JOIN ENCARGADO ON REPORTE.ENCARGADO_ID = ENCARGADO.ID\n" +
                        "JOIN COMPUTADORA ON REPORTE.COMPUTADORA_ID = COMPUTADORA.ID;";
                statement = connection.prepareStatement(sql);
                // Ejecutar la consulta
                resultSet = statement.executeQuery();
                // Recorrer el resultado y crear los objetos encargado
                while (resultSet.next()) {
                    Reporte reportitos = new Reporte();
                    reportitos.setID((resultSet.getInt("Numero de Reporte")));
                    reportitos.setTITULO(resultSet.getString("Titulo"));
                    reportitos.setDESCRIPCION(resultSet.getString("Descripcion"));
                    String fecha = resultSet.getString("Fecha");
                    reportitos.setFECHA(fecha);
                    String hora = resultSet.getString("Hora");
                    reportitos.setHORA(hora);
                    reportitos.setNOMBRE(resultSet.getString("Nombre"));
                    reportitos.setAPELLIDO_P(resultSet.getString("ApellidoPat"));
                    reportitos.setAPELLIDO_M(resultSet.getString("ApellidoMat"));
                    reportitos.setCOMPUTADOORA(resultSet.getInt("Compu"));
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
                    Log.e("Error", "Error al cerrar la conexión: " + e.toString());
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