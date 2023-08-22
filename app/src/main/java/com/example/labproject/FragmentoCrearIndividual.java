package com.example.labproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragmentoCrearIndividual extends Fragment {

    /*Conexion con BD*/
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@192.168.3.11:1521/XEPDB1"; //LUIS
    //private static final String URL = "jdbc:oracle:thin:@192.168.3.11:1521/XEPDB1"; //SERVICIO SOCIAL
    private static final String USERNAME = "ENCARGADO";
    private static final String PASSWORD = "ENCARGADO";
    SharedPreferences sharedPreferences;

    TextView nombreEnc, apePaEnc, apeMaEnc;
    MaterialButton escanear;
    RadioButton laboratorio1, laboratorio2;
    private String radioButtonMessage = ""; //Variable para almacenar el laboratorio

    public FragmentoCrearIndividual() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragmento_crear_individual, container, false);
        // ASIGNACION DE MOSTRAR NOMBRE DEL ENCARGADO
        sharedPreferences = requireContext().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        nombreEnc = view.findViewById(R.id.encNombre);
        apePaEnc = view.findViewById(R.id.encApeP);
        apeMaEnc = view.findViewById(R.id.encApeM);
        String nombreUsuario = sharedPreferences.getString("usuario", "");

        if(!nombreUsuario.isEmpty()){
            new ConsultaBaseDatosTask().execute(nombreUsuario);
        }

        // ASIGNACION DE HORA Y FECHA ACTUALES
        TextView fechaEntradaTextView = view.findViewById(R.id.fechaEntradaTextView);
        TextView horaEntradaTextView = view.findViewById(R.id.horaEntradaTextView);

        // Obtén la fecha actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String fechaActual = dateFormat.format(new Date());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String horaActual = timeFormat.format(new Date());

        // Establece la fecha actual en el TextView
        fechaEntradaTextView.setText(fechaActual);
        horaEntradaTextView.setText(horaActual);


        // Asignando variables al radiobutton
        laboratorio1 = view.findViewById(R.id.radioButtonLab1);
        laboratorio2 = view.findViewById(R.id.radioButtonLab2);

        View.OnClickListener radioClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        };

        laboratorio1.setOnClickListener(radioClickListener);
        laboratorio2.setOnClickListener(radioClickListener);

        // Asignado variable al boton
        escanear = view.findViewById(R.id.escanearBoton);
        escanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Aqui se manda a llamar a la camara

                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(FragmentoCrearIndividual.this);
                integrator.setPrompt("Escanear Codigo QR");
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setCameraId(0);
                integrator.initiateScan();
            }
        });

        return view;
    }

    private class ConsultaBaseDatosTask extends AsyncTask<String, Void, String[]>{
        @Override
        protected String[] doInBackground(String... strings) {
            String nombreUsuario = strings[0];
            String[] nombresApellidos = new String[3];
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try {
                // Cargar el controlador JDBC de Oracle
                Class.forName(DRIVER);
                // Establecer la conexión a la base de datos
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                // Preparar la consulta SQL para seleccionar los encargados
                String sql = "SELECT nombre, apellido_p, apellido_m FROM ENCARGADO WHERE usuario = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, nombreUsuario);

                // Ejecutar la consulta y obtener el resultado
                resultSet = statement.executeQuery();

                // Verificar si hay un resultado válido
                if (resultSet.next()) {
                    nombresApellidos[0] = resultSet.getString("nombre");
                    nombresApellidos[1] = resultSet.getString("apellido_p");
                    nombresApellidos[2] = resultSet.getString("apellido_m");
                }

            }catch (Exception e){
                Log.e("Error", "Error en la consulta: " + e.toString());
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
                    Log.e("Error", "Error en la consulta: " + e.toString());
                }
            }
            return nombresApellidos;
        }

        @Override
        protected void onPostExecute(String[] nombresApellidos) {
            if (nombresApellidos != null) {
                nombreEnc.setText(nombresApellidos[0]);
                apePaEnc.setText(nombresApellidos[1]);
                apeMaEnc.setText(nombresApellidos[2]);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Escaneo Cancelado", Toast.LENGTH_SHORT).show();
            } else {
                String qrContent = result.getContents();

                try {

                    // Aqui se establece que es lo que se va a buscar en la pagina de acuerdo a su html  ---->

                    Document doc = Jsoup.connect(qrContent).get();
                    Element nombreElement = doc.selectFirst(".nombre");
                    Element boletaElement = doc.selectFirst(".boleta");
                    Element carreraElement = doc.selectFirst(".carrera");

                    String nombre = nombreElement != null ? nombreElement.text() : "Nombre no encontrado";
                    String boleta = boletaElement != null ? boletaElement.text() : "Boleta no encontrada";
                    String carrera = carreraElement != null ? carreraElement.text() : "Carrera no encontrada";


                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        if (checked) {
            if (view == laboratorio1) {
                radioButtonMessage = "Laboratorio 1 asignado";
            } else if (view == laboratorio2) {
                radioButtonMessage = "Laboratorio 2 asignado";
            }
        }

        Toast.makeText(getActivity(), radioButtonMessage, Toast.LENGTH_SHORT).show();
    }
}

