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
    private String nombre = "", boleta = "", carrera = "", apePa = "", apeMa = "", nombreP = "", apePaP = "", apeMaP = "", boletaP = "";
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
                // Crea una instancia par Obtener el numero de computadora
                //ConsultaComputadora consultaCompu = new ConsultaComputadora();
                //consultaCompu.execute();
                Toast.makeText(requireContext(), "Alumno " + nombre + "Registrado", Toast.LENGTH_SHORT).show();
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
                }

                Log.e("Alumno encontrado en base de datos", "Alumno encontrado: " + nombre);

            } catch(Exception e){
                Log.e("ERROR EN BUSCAR ALUMNO", String.valueOf(e));
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
                    Log.e("Error", "Error en la consulta: " + e);
                }
            }

            if (boleta.isEmpty()){

                Log.e("Buscando alumno", "Buscando alumno por que boleta == Empty");

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

                    Log.e("Datos de webScraping obtenidos", "Datos obtenidos satisfactoriamente");

                } catch (IOException e){
                    Log.d("Error de web scrapping","Este es el eror: " + e);
                }

                try {
                    Class.forName(DRIVER);
                    connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                    String sql = "insert into estudiante(nombre, apellido_p, apellido_m, boleta, qr, carrera_id) values (?, ?, ?, ?, ?, 1)";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, nombre);
                    statement.setString(2, apePa);
                    statement.setString(3, apeMa);
                    statement.setString(4, boleta);
                    statement.setString(5, url);
                    statement.executeUpdate();

                    Log.e("Guardar datos alumno", "Datos guardados correctamente");

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

                        Log.e("Datos de webScraping obtenidos", "Datos obtenidos satisfactoriamente");

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
}