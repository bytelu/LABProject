package com.example.labproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.labproject.NameParser.parse;
import com.example.labproject.res.CData;
import com.example.labproject.sesindividual.SesionIndividual;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class FragmentoSesiones extends Fragment {
    /*Conexion con BD*/
    private static final String DRIVER = CData.getDriver();
    private static final String URL = CData.getUrl();
    private static final String USERNAME = CData.getUsername();
    private static final String PASSWORD = CData.getPassword();
    SharedPreferences sharedPreferences;
    TextView nombreEnc, apePaEnc, apeMaEnc, nombreAlu, apePaAlu, apeMaAlu, carreraAlu, boletaAlu, nombreProf, apePaProf, apeMaProf, boletaProf;
    MaterialButton escanearAlu, escanearProf, guardar;
    RadioButton laboratorio1, laboratorio2;
    private String radioButtonMessage = ""; //Variable para almacenar el laboratorio
    private String nombre = "", boleta = "", carrera = "", apePa = "", apeMa = "", nombreP = "", apePaP = "", apeMaP = "", boletaP = "", idAlumno, idProfesor, computadoraId;
    private int opcionElegida;
    private static final int OPCION_PROFESOR = 1;
    private static final int OPCION_ALUMNO = 2;
    public FragmentoSesiones() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmento_sesiones, container, false);
        /* VARIABLES ENCARGADO*/
        sharedPreferences = requireContext().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        nombreEnc = view.findViewById(R.id.encNombre);
        apePaEnc = view.findViewById(R.id.encApeP);
        apeMaEnc = view.findViewById(R.id.encApeM);
        String nombreUsuario = sharedPreferences.getString("usuario", "");
        /* VARIABLES PROFESOR*/
        nombreProf = view.findViewById(R.id.profNombre);
        apePaProf = view.findViewById(R.id.profApeP);
        apeMaProf = view.findViewById(R.id.profApeM);
        /* VARIABLES ALUMNO */
        nombreAlu = view.findViewById(R.id.nombreAlu);
        apePaAlu = view.findViewById(R.id.apePaternoAlu);
        apeMaAlu = view.findViewById(R.id.apeMaAlu);
        carreraAlu = view.findViewById(R.id.carreraAlu);
        boletaAlu = view.findViewById(R.id.boletaAlu);
        /* VARIABLES LABORATORIOS*/
        laboratorio1 = view.findViewById(R.id.radioButtonLab1);
        laboratorio2 = view.findViewById(R.id.radioButtonLab2);
        /* VARIABLES BOTONES*/
        escanearProf = view.findViewById(R.id.escanearProfBoton);
        escanearAlu = view.findViewById(R.id.escanearBoton);
        guardar = view.findViewById(R.id.botonGuardar);
        /* OBTENER NOMBRE DE ENCARGADO */
        if (!nombreUsuario.isEmpty()) {
            new FragmentoSesiones.ConsultaBaseDatosTask().execute(nombreUsuario);
        }
        /* OBTENER FECHAS Y HORA ACTUAL */
        TextView fechaEntradaTextView = view.findViewById(R.id.fechaEntradaTextView);
        TextView horaEntradaTextView = view.findViewById(R.id.horaEntradaTextView);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String fechaActual = dateFormat.format(new Date());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String horaActual = timeFormat.format(new Date());
        fechaEntradaTextView.setText(fechaActual);
        horaEntradaTextView.setText(horaActual);
        /* OBTENER LABORATORIO ELEGIDO */
        View.OnClickListener radioClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        };
        laboratorio1.setOnClickListener(radioClickListener);
        laboratorio2.setOnClickListener(radioClickListener);
        /*  ASGINACION VARIABLE AL BOTON DEL PROFESOR */
        escanearProf.setOnClickListener(view1 -> {
            opcionElegida = OPCION_PROFESOR;
            escanearCodigoQRProfesor();
        });
        /* ASIGNACION VARIABLE AL BOTON DEL ALUMNO */
        escanearAlu.setOnClickListener(view1 -> {
            opcionElegida = OPCION_ALUMNO;
            escanearCodigoQRAlumno();
        });
        /* ASIGNACION VARIABLE AL BOTON DE GUARDAR */
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idAlumno != null || !idAlumno.isEmpty()) {
                    // Verificar que se haya elegido una opción de laboratorio
                    if (laboratorio1.isChecked() || laboratorio2.isChecked()) {
                        // Ambas condiciones se cumplen, puedes mostrar el mensaje de ingreso
                        Toast.makeText(requireContext(), nombre + " ingresó a la sesión correctamente", Toast.LENGTH_SHORT).show();
                        Log.e("Datos", "Profesor de sesión " + idProfesor);
                        Log.e("Datos", "Alumno de sesión " + idAlumno);
                        Log.e("Datos", "Laboratorio 1 " + laboratorio1.isChecked());
                        Log.e("Datos", "Laboratorio 2 " + laboratorio2.isChecked());
                        FragmentoSesiones.ConsultaComputadora consultaCompu = new FragmentoSesiones.ConsultaComputadora();
                        consultaCompu.execute();
                    } else {
                        // No se eligió una opción de laboratorio
                        Toast.makeText(requireContext(), "Selecciona un laboratorio para generar la sesión", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // idAlumno no tiene un valor
                    Toast.makeText(requireContext(), "Escanea el código QR del Alumno", Toast.LENGTH_SHORT).show();
                }
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
                // Establecer la conexion a la base de datos
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

            } catch (Exception e) {
                Log.e("Error", "Error en la consulta: " + e.toString());
            } finally {
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
            super.onPostExecute(nombresApellidos);
            if (nombresApellidos != null) {
                nombreEnc.setText(nombresApellidos[0]);
                apePaEnc.setText(nombresApellidos[1]);
                apeMaEnc.setText(nombresApellidos[2]);
            }
        }
    }
    public void onRadioButtonClicked(View view){
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
    private void escanearCodigoQRProfesor() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(FragmentoSesiones.this);
        integrator.setPrompt("Escanear Código QR del Profesor");
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setCameraId(0);
        integrator.initiateScan();
    }
    private void escanearCodigoQRAlumno() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(FragmentoSesiones.this);
        integrator.setPrompt("Escanear Codigo QR del Alumno");
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setCameraId(0);
        integrator.initiateScan();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String url = result.getContents();
                Log.d("MiApp", "Contenido de URL " + url);
                if (opcionElegida == OPCION_PROFESOR) {
                    Log.d("Profesor", "Se ingreso a la opción del profesor");
                    // Procesa el código QR del profesor
                    try{
                        Log.d("qrContent","Este es el contenido del qr: " + url);
                        new FragmentoSesiones.BuscarProfesorTask().execute(url);
                    } catch (Exception e){
                        Log.d("Tag","Este es el eror: " + e);
                    }
                } else if (opcionElegida == OPCION_ALUMNO) {
                    Log.d("Alumno", "Se ingreso a la opción del alumno");
                    // Procesa el código QR del alumno
                    try{
                        Log.d("qrContent","Este es el contenido del qr: " + url);
                        new FragmentoSesiones.BuscarAlumnoTask().execute(url);
                    } catch (Exception e){
                        Log.d("Tag","Este es el eror: " + e);
                    }
                } else {
                    Log.d("Desconocido", "Se ingreso a la opción del desconocido");
                }
            } else {
                // El escaneo ha sido cancelado, muestra un mensaje al usuario.
                Toast.makeText(getContext(), "Escaneo cancelado", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private class BuscarAlumnoTask extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            int alumnoEncontrado = 0;
            try{
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                String sql = "select nombre, boleta, apellido_p, apellido_m, carrera_id from estudiante where qr = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, url);
                resultSet = statement.executeQuery();

                if (resultSet.next()){
                    nombre = resultSet.getString("nombre");
                    boleta = resultSet.getString("boleta");
                    apePa = resultSet.getString("apellido_p");
                    apeMa = resultSet.getString("apellido_m");
                    alumnoEncontrado = 1;
                }

                Log.e("Alumno", "Alumno con nombre?: " + nombre);

            } catch(Exception e){
                Log.e("ERROR EN BUSCAR ALUMNO", String.valueOf(e));
            }finally {
                // Cerrar los recursos
                try {
                    if (resultSet != null) resultSet.close();
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (Exception e) {
                    Log.e("Error", "Error en la consulta: " + e);
                }
            }

            if (alumnoEncontrado == 1){
                if (boleta.isEmpty() || nombre.isEmpty()){

                    Log.e("Actualizando alumno", "Buscando alumno por que boleta == Empty");

                    System.setProperty("jsse.enableSNIExtension", "false");
                    System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,TLSv1");
                    TrustManager[] trustAllCertificates = new TrustManager[] {
                            new X509TrustManager() {
                                public X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }
                                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                                }
                                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                                }
                            }
                    };

                    try {
                        SSLContext sslContext = SSLContext.getInstance("TLS");
                        sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
                        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {

                        org.jsoup.Connection.Response response = Jsoup.connect(url).execute();
                        Log.d("Connection.Response", "doInBackground: Connections.Response pasado");

                        if (response.statusCode() == 200){
                            Document document = response.parse();
                            Elements nombreElement = document.select(".nombre");
                            Elements boletaElement = document.select(".boleta");
                            Elements carreraElement = document.select(".carrera");

                            nombre = nombreElement.text();
                            boleta = boletaElement.text();
                            carrera = carreraElement.text();

                        }

                        //aqui separar el nombre completo en : nombre,apePA, apeMA.
                        List<String> fullname = parse.nameParser(nombre);
                        nombre = fullname.get(0);
                        apePa = fullname.get(1);
                        apeMa = fullname.get(2);

                        Log.e("Alumno webScraping obtenido", "Datos obtenidos satisfactoriamente");

                    } catch (IOException e){
                        Log.d("Error de web scrapping","Este es el eror: " + e);
                    }

                    try {
                        Class.forName(DRIVER);
                        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                        String sql = "UPDATE estudiante SET nombre = ?, apellido_p = ?, apellido_m = ?, boleta = ?, carrera_id = 1 WHERE qr = ?";
                        statement = connection.prepareStatement(sql);
                        statement.setString(1, nombre);
                        statement.setString(2, apePa);
                        statement.setString(3, apeMa);
                        statement.setString(4, boleta);
                        statement.setString(5, url);
                        statement.executeUpdate();

                        Log.e("Actualizar datos alumno", "Datos actualizados correctamente");

                    } catch (Exception e){
                        Log.e("Error", "Error al guardar datos de web scraping: " + e);
                    } finally {
                        // Cerrar los recursos
                        try {
                            if (statement != null) {
                                statement.close();
                            }
                            if (connection != null) {
                                connection.close();
                            }
                        } catch (Exception e) {
                            Log.e("Error", "Error al cerrar conexión: " + e);
                        }
                    }
                }
            }else {
                Log.e("Alumno NO encontrado en base de datos", "Alumno no encontrado en la base de datos.");
                Log.e("Agregando Alumno", "Agregando Alumno porque no se encontro");

                System.setProperty("jsse.enableSNIExtension", "false");
                System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,TLSv1");
                TrustManager[] trustAllCertificates = new TrustManager[] {
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }
                            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            }
                            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            }
                        }
                };

                try {
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    org.jsoup.Connection.Response response = Jsoup.connect(url).execute();
                    Log.d("Connection.Response", "doInBackground: Connections.Response pasado");

                    if (response.statusCode() == 200){
                        Document document = response.parse();
                        Elements nombreProElement = document.select(".nombre");
                        Elements boletaProElement = document.select(".boleta");
                        Elements carreraProElement = document.select(".carrera");
                        nombre = nombreProElement.text();
                        boleta = boletaProElement.text();
                        carrera = carreraProElement.text();
                    }

                    //aqui separar el nombre completo en : nombre,apePA, apeMA.
                    List<String> fullname = parse.nameParser(nombre);
                    nombre = fullname.get(0);
                    apePa = fullname.get(1);
                    apeMa = fullname.get(2);

                    Log.e("Datos de webScraping obtenidos", "Datos obtenidos satisfactoriamente");

                } catch (IOException e){
                    Log.d("Error de web scrapping","Este es el eror: " + e);
                }

                try {
                    Class.forName(DRIVER);
                    connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                    String sql = "INSERT INTO estudiante (nombre, apellido_p, apellido_m, boleta, qr, carrera_id) VALUES (?, ?, ?, ?, ?, 1)";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, nombre);
                    statement.setString(2, apePa);
                    statement.setString(3, apeMa);
                    statement.setString(4, boleta);
                    //statement.setString(5, carrera);
                    statement.setString(5, url);
                    statement.executeUpdate();
                    Log.e("Guardar datos Alumno", "Datos guardados correctamente");
                } catch (Exception e){
                    Log.e("Error", "Error al guardar datos de web scraping: " + e);
                } finally {
                    // Cerrar los recursos
                    try {
                        if (statement != null) {
                            statement.close();
                        }
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (Exception e) {
                        Log.e("Error", "Error al cerrar conexión: " + e);
                    }
                }
            }
            /* OBTENIENDO ID Y NOMBRE DE CARRERA */
            try {
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                String sql = "SELECT estudiante.id, carrera.carrera " +
                        "FROM estudiante " +
                        "INNER JOIN carrera ON estudiante.carrera_id = carrera.id " +
                        "WHERE estudiante.boleta = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, boleta);
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    idAlumno = resultSet.getString("estudiante.id");
                    carrera = resultSet.getString("carrera.carrera").toUpperCase();
                    // Ahora tienes el id del alumno, el nombre de la carrera y la boleta del estudiante
                    Log.e("ID Alumno", "ID del alumno: " + idAlumno);
                    Log.e("Nombre Carrera", "Nombre de la carrera: " + carrera);
                }else{
                    Log.e("Estudiante no encontrado", "No se encontró el estudiante con la boleta proporcionada");
                }
            } catch (Exception e){
                Log.e("Error", "Error al guardar datos ID y Carrera del alumno: " + e);
            } finally {
                // Cerrar los recursos
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception e) {
                    Log.e("Error", "Error al cerrar conexión: " + e);
                }
            }
            Log.e("Datos", "Nombre " + nombre);
            Log.e("Datos", "Apellido Pa " + apePa);
            Log.e("Datos", "Apellido Ma " + apeMa);
            Log.e("Datos", "Boleta " + boleta);
            Log.e("Datos", "Carrera " + carrera);
            return null;
        }

        @Override
        protected void onPostExecute(Void alumno) {
            super.onPostExecute(alumno);
            nombreAlu.setText(nombre);
            apePaAlu.setText(apePa);
            apeMaAlu.setText(apeMa);
            boletaAlu.setText(boleta);
            carreraAlu.setText(carrera);
            Log.e("Existe profesor", "Id del profesor " + idProfesor);
        }
    }
    private class BuscarProfesorTask extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            int profesorEncontrado = 0;
            try{
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                String sql = "SELECT nombre, boleta, apellido_p, apellido_m FROM profesor WHERE qr = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, url);
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    // Si se encuentra un profesor con la URL proporcionada, actualiza las variables
                    nombreP = resultSet.getString("nombre");
                    boletaP = resultSet.getString("boleta");
                    apePaP = resultSet.getString("apellido_p");
                    apeMaP = resultSet.getString("apellido_m");
                    // Marca que el profesor fue encontrado
                    profesorEncontrado = 1;
                }

                Log.e("Profesor encontrado en base de datos", "Profesor encontrado: " + nombreP);

            } catch(Exception e){
                Log.e("ERROR EN BUSCAR PROFESOR", String.valueOf(e));
            }finally {
                // Cerrar los recursos
                try {
                    if (resultSet != null) resultSet.close();
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (Exception e) {
                    Log.e("Error", "Error en la consulta: " + e);
                }
            }
            if (profesorEncontrado == 1) {
                if (boletaP.isEmpty() || nombreP.isEmpty() ){

                    Log.e("Actualizando profesor", "Buscando profesor porque la Boleta esta vacia");

                    System.setProperty("jsse.enableSNIExtension", "false");
                    System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,TLSv1");
                    TrustManager[] trustAllCertificates = new TrustManager[] {
                            new X509TrustManager() {
                                public X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }
                                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                                }
                                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                                }
                            }
                    };

                    try {
                        SSLContext sslContext = SSLContext.getInstance("TLS");
                        sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
                        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {

                        org.jsoup.Connection.Response response = Jsoup.connect(url).execute();
                        Log.d("Connection.Response", "doInBackground: Connections.Response pasado");

                        if (response.statusCode() == 200){
                            Document document = response.parse();
                            Elements nombreProElement = document.select(".nombre");
                            Elements boletaProElement = document.select(".boleta");
                            nombreP = nombreProElement.text();
                            boletaP = boletaProElement.text();
                        }

                        //aqui separar el nombre completo en : nombre,apePA, apeMA.
                        List<String> fullname = parse.nameParser(nombreP);
                        nombreP = fullname.get(0);
                        apePaP = fullname.get(1);
                        apeMaP = fullname.get(2);

                        Log.e("Profesor webScraping obtenido", "Datos obtenidos satisfactoriamente");

                    } catch (IOException e){
                        Log.d("Error de web scrapping","Este es el eror: " + e);
                    }

                    try {
                        Class.forName(DRIVER);
                        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                        String sql = "UPDATE profesor SET nombre = ?, apellido_p = ?, apellido_m = ?, boleta = ? WHERE qr = ?";
                        statement = connection.prepareStatement(sql);
                        statement.setString(1, nombreP);
                        statement.setString(2, apePaP);
                        statement.setString(3, apeMaP);
                        statement.setString(4, boletaP);
                        statement.setString(5, url);
                        statement.executeUpdate();
                        Log.e("Actualizar datos de profesor", "Datos actualizados correctamente");
                    } catch (Exception e){
                        Log.e("Error", "Error al guardar datos de web scraping: " + e);
                    } finally {
                        // Cerrar los recursos
                        try {
                            if (statement != null) {
                                statement.close();
                            }
                            if (connection != null) {
                                connection.close();
                            }
                        } catch (Exception e) {
                            Log.e("Error", "Error al cerrar conexión: " + e);
                        }
                    }
                }
            } else {
                Log.e("Profesor NO encontrado en base de datos", "Profesor no encontrado en la base de datos.");
                Log.e("Agregando profesor", "Agregando profesor porque no se encontro");

                System.setProperty("jsse.enableSNIExtension", "false");
                System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,TLSv1");
                TrustManager[] trustAllCertificates = new TrustManager[] {
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }
                            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            }
                            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            }
                        }
                };

                try {
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    org.jsoup.Connection.Response response = Jsoup.connect(url).execute();
                    Log.d("Connection.Response", "doInBackground: Connections.Response pasado");

                    if (response.statusCode() == 200){
                        Document document = response.parse();
                        Elements nombreProElement = document.select(".nombre");
                        Elements boletaProElement = document.select(".boleta");
                        nombreP = nombreProElement.text();
                        boletaP = boletaProElement.text();
                    }

                    //aqui separar el nombre completo en : nombre,apePA, apeMA.
                    List<String> fullname = parse.nameParser(nombreP);
                    nombreP = fullname.get(0);
                    apePaP = fullname.get(1);
                    apeMaP = fullname.get(2);

                    Log.e("Datos de webScraping obtenidos", "Datos obtenidos satisfactoriamente");

                } catch (IOException e){
                    Log.d("Error de web scrapping","Este es el eror: " + e);
                }

                try {
                    Class.forName(DRIVER);
                    connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                    String sql = "INSERT INTO profesor (nombre, apellido_p, apellido_m, boleta, qr) VALUES (?, ?, ?, ?, ?)";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, nombreP);
                    statement.setString(2, apePaP);
                    statement.setString(3, apeMaP);
                    statement.setString(4, boletaP);
                    statement.setString(5, url);
                    statement.executeUpdate();
                    Log.e("Guardar datos profesor", "Datos guardados correctamente");
                } catch (Exception e){
                    Log.e("Error", "Error al guardar datos de web scraping: " + e);
                } finally {
                    // Cerrar los recursos
                    try {
                        if (statement != null) {
                            statement.close();
                        }
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (Exception e) {
                        Log.e("Error", "Error al cerrar conexión: " + e);
                    }
                }
            }
            try {
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                String sql = "SELECT profesor.id " +
                        "FROM profesor " +
                        "WHERE profesor.boleta = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, boletaP);
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    idProfesor = resultSet.getString("profesor.id");
                    // Ahora tienes el id del alumno, el nombre de la carrera y la boleta del estudiante
                    Log.e("ID Profesor", "ID del Profesor: " + idProfesor);
                }else{
                    Log.e("Profesor no encontrado", "No se encontró el profesor con la boleta proporcionada");
                }
            } catch (Exception e){
                Log.e("Error", "Error al guardar ID del profesor: " + e);
            } finally {
                // Cerrar los recursos
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception e) {
                    Log.e("Error", "Error al cerrar conexión: " + e);
                }
            }
            Log.e("Datos", "Nombre " + nombreP);
            Log.e("Datos", "Apellido Pa " + apePaP);
            Log.e("Datos", "Apellido Ma " + apeMaP);
            Log.e("Datos", "Boleta " + boletaP);
            return null;
        }

        @Override
        protected void onPostExecute(Void profesor) {
            super.onPostExecute(profesor);
            nombreProf.setText(nombreP);
            apePaProf.setText(apePaP);
            apeMaProf.setText(apeMaP);
        }
    }
    private class ConsultaComputadora extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... voids) {
            // Aquí realiza la consulta a la base de datos y obtén el mensaje
            String computadora = "";
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                // Cargar el controlador JDBC de Oracle
                Class.forName(DRIVER);
                // Establecer la conexión a la base de datos
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                // Preparar la consulta SQL para seleccionar la primera computadora disponible
                String sql = "SELECT id, numero\n" +
                        "FROM COMPUTADORA\n" +
                        "WHERE estado = 1\n" +
                        "AND ocupada = 0 \n" +
                        "AND laboratorio = ? \n" +
                        "LIMIT 1\n";
                statement = connection.prepareStatement(sql);
                Log.d("TAG", "RadioButton: "+ radioButtonMessage);
                if (radioButtonMessage.equals("Laboratorio 1 asignado")){
                    statement.setInt(1, 1);
                    Log.d("TAG", "Este es laboratorio 1");
                }else{
                    statement.setInt(1, 2);
                    Log.d("TAG", "Este es laboratorio 2");
                }

                // Ejecutar la consulta y obtener el resultado
                resultSet = statement.executeQuery();

                // Verificar si hay un resultado válido
                if (resultSet.next()) {
                    computadoraId = resultSet.getString("id");
                    computadora = resultSet.getString("numero");
                    Log.d("TAG", "Computadora Id.: "+ computadoraId);
                    Log.d("TAG", "Computadora No.: "+ computadora);
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
                    Log.e("Error", "Error en la consultaa: " + e.toString());
                }
            }
            return computadora;
        }

        @Override
        protected void onPostExecute(String resultado) {
            // Aquí puedes llamar a un método para mostrar la ventana flotante con el resultado
            Log.d("TAG", "On post Computadora No.: "+ resultado);
            Log.d("TAG", "On post Computadora ID.: "+ computadoraId);
            mostrarComputadora(resultado);
            // Una vez que se mostró la computadora, ejecuta la AsyncTask para actualizar a computadora"ocupada" y crear sesion
            FragmentoSesiones.ActualizarOcupada actualizarOcupada = new FragmentoSesiones.ActualizarOcupada();
            actualizarOcupada.execute();
        }
    }
    private void mostrarComputadora(String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(Html.fromHtml("<center>Confirmación de Sesión</center>"));

        // Crear un diseño de LinearLayout vertical
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);
        // Crear TextViews para mostrar la información
        TextView alumnoTextView = new TextView(requireContext());
        TextView laboratorioTextView = new TextView(requireContext());
        TextView computadoraTextView = new TextView(requireContext());
        // Establecer los valores dinámicos
        String alumnoName = nombre + " " + apePa + " " + apeMa; // Reemplaza con el nombre completo del alumno
        String laboratorioName;
        if (laboratorio1.isChecked()) {
            laboratorioName = "Laboratorio 1";
        } else if (laboratorio2.isChecked()) {
            laboratorioName = "Laboratorio 2";
        } else {
            // En caso de que ninguno de los laboratorios esté seleccionado
            laboratorioName = "Laboratorio no seleccionado";
        }
        String computerNumber = mensaje; // Reemplaza con el número de la computadora
        alumnoTextView.setText("El alumno: " + alumnoName);
        laboratorioTextView.setText("Ha ingresado al: " + laboratorioName);
        computadoraTextView.setText("Se le ha asignado la computadora: " + computerNumber);
        // Agregar TextViews al diseño
        layout.addView(alumnoTextView);
        layout.addView(computadoraTextView);
        builder.setView(layout); // Establecer el diseño personalizado
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            // Aquí puedes realizar acciones adicionales si el usuario hace clic en Aceptar
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private class ActualizarOcupada extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... voids) {
            Connection connection = null;
            PreparedStatement statement = null;
            Log.d("TAG", "ID Computadora en Actualizar: "+ computadoraId);
            try {
                // Cargar el controlador JDBC de Oracle
                Class.forName(DRIVER);
                // Establecer la conexión a la base de datos
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                // Preparar la consulta SQL para actualizar la columna "ocupada" a 1
                String sql = "UPDATE COMPUTADORA SET ocupada = 1 WHERE id = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, computadoraId);
                // Ejecutar la consulta para actualizar
                statement.executeUpdate();

            } catch (Exception e) {
                Log.e("Error", "Error al actualizar ocupada: " + e.toString());
            } finally {
                // Cerrar los recursos
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception e) {
                    Log.e("Error", "Error al cerrar conexión: " + e.toString());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("Crear sesion", "Crear sesion iniciado");
            FragmentoSesiones.CrearSesion crearSesion = new FragmentoSesiones.CrearSesion();
            crearSesion.execute();
        }
    }
    private class CrearSesion extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            Connection connection = null;
            PreparedStatement statement = null;

            try {
                // Cargar el controlador JDBC de Oracle
                Class.forName(DRIVER);
                // Establecer la conexión a la base de datos
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String fechaActual = dateFormat.format(new Date());
                String horaActual = timeFormat.format(new Date());
                String encargadoId = null;

                String consultaEncargado = "SELECT id FROM ENCARGADO WHERE usuario = ?";
                PreparedStatement encargadoStatement = connection.prepareStatement(consultaEncargado);
                encargadoStatement.setString(1, sharedPreferences.getString("usuario", ""));
                ResultSet encargadoResultSet = encargadoStatement.executeQuery();
                if (encargadoResultSet.next()) {
                    encargadoId = encargadoResultSet.getString("id");
                }
                String sql;

                if (idProfesor == null) {
                    // Si el profesor es nulo, excluye el campo profesor_id de la consulta
                    sql = "INSERT INTO sesion(fecha, hora_inicio, encargado_id, estudiante_id, computadora_id, activo) " +
                            "VALUES (?, ?, ?, ?, ?, 1)";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, fechaActual);
                    statement.setString(2, horaActual);
                    statement.setString(3, encargadoId);
                    statement.setString(4, idAlumno);
                    statement.setString(5, computadoraId);
                } else {
                    // Si el profesor no es nulo, incluye el campo profesor_id en la consulta
                    sql = "INSERT INTO sesion(fecha, hora_inicio, encargado_id, estudiante_id, profesor_id, computadora_id, activo) " +
                            "VALUES (?, ?, ?, ?, ?, ?, 1)";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, fechaActual);
                    statement.setString(2, horaActual);
                    statement.setString(3, encargadoId);
                    statement.setString(4, idAlumno);
                    statement.setString(5, idProfesor);
                    statement.setString(6, computadoraId);
                }
                // Ejecutar la consulta para actualizar
                statement.executeUpdate();


                Log.e("Sesion creada", "Sesion creada correctamente");

            } catch (Exception e) {
                Log.e("Error", "Error al crear sesion: " + e.toString());
            } finally {
                // Cerrar los recursos
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception e) {
                    Log.e("Error", "Error al cerrar conexión: " + e.toString());
                }
            }

            return null;
        }
    }
}