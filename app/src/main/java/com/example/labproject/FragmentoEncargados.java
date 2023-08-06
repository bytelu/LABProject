package com.example.labproject;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class FragmentoEncargados extends Fragment {
    /*Conexion con BD*/
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@192.168.100.74:1521/XEPDB1";
    private static final String USERNAME = "ENCARGADO";
    private static final String PASSWORD = "ENCARGADO";
    private Connection connection;
    private TextView textView;

    public FragmentoEncargados() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmento_encargados, container, false);

        // Obtener referencia al TextView desde la vista inflada del fragmento
        textView = view.findViewById(R.id.textEncargados);
        // Realizar la consulta y mostrar los datos en el TextView
        new ConexionAsyncTask().execute();
        return view;
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_fragmento_encargados, container, false);
    }

    // AsyncTask para realizar la conexión y consulta en segundo plano
    private class ConexionAsyncTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            String resultadoConsulta = "";
            try {
                resultadoConsulta = consultaEncargados();
            }catch (Exception e){
                // Manejo de errores en caso de problemas con la conexión o consulta
                resultadoConsulta = "Error: " + e.toString();
            }
            return resultadoConsulta;
        }

        @Override
        protected void onPostExecute(String resultado) {
            // Actualizar la interfaz de usuario con los resultados de la consulta
            textView.setText(resultado);
        }
    }

    public String consultaEncargados() {
        String resultadoConsulta = "";
        try {
            Class.forName(DRIVER);
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            Log.d("Conexion", "Conexión a la base de datos exitosa");

            Statement statement = connection.createStatement();
            StringBuffer stringBuffer = new StringBuffer();
            ResultSet resultSet = statement.executeQuery("select cod_cpu from COMPUTADORA");
            while (resultSet.next()){
                stringBuffer.append(resultSet.getString(1)+"\n");
            }

            resultadoConsulta = stringBuffer.toString();
            connection.close();

            Log.d("Consulta", "Consulta exitosa: " + resultadoConsulta);
        }
        catch (Exception e){
            resultadoConsulta = "Error: " + e.toString();
            Log.e("Error", "Error en la consulta: " + e.toString());
        }
        return resultadoConsulta;
    }
}