package com.example.labproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.labproject.res.CData;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jsoup.Connection.Response;
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
import com.example.labproject.NameParser.parse;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class FragmentoCrearIndividual extends Fragment {

    /*Conexion con BD*/
    private static final String DRIVER = CData.getDriver();
    private static final String URL = CData.getUrl();
    private static final String USERNAME = CData.getUsername();
    private static final String PASSWORD = CData.getPassword();
    SharedPreferences sharedPreferences;

    TextView nombreEnc, apePaEnc, apeMaEnc, nombreAlu, apePaAlu, apeMaAlu, carreraAlu, boletaAlu;
    MaterialButton escanear, guardar;
    RadioButton laboratorio1, laboratorio2;
    private String radioButtonMessage = ""; //Variable para almacenar el laboratorio
    private String nombre = "", boleta = "", carrera="", apePa="", apeMa="";

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

        // Asignado variable al boton de alumno
        escanear = view.findViewById(R.id.escanearBoton);
        escanear.setOnClickListener(view1 -> {

            // Aqui se manda a llamar a la camara

            IntentIntegrator integrator = IntentIntegrator.forSupportFragment(FragmentoCrearIndividual.this);
            integrator.setPrompt("Escanear Codigo QR");
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setCameraId(0);
            integrator.initiateScan();
        });

        /*---------------------------------------------------*/
        //ASIGNACION DE VARIABLES DE ALUMNOS
        nombreAlu = view.findViewById(R.id.nombreAlu);
        apePaAlu = view.findViewById(R.id.apePaternoAlu);
        apeMaAlu = view.findViewById(R.id.apeMaAlu);
        carreraAlu = view.findViewById(R.id.carreraAlu);
        boletaAlu = view.findViewById(R.id.boletaAlu);
        /*----------------------------------------------------*/

        guardar = view.findViewById(R.id.botonGuardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea una instancia par Obtener el numero de computadora
                ConsultaComputadora consultaCompu = new ConsultaComputadora();
                consultaCompu.execute();
                Toast.makeText(requireContext(), "Alumno " + nombre + "Registrado", Toast.LENGTH_SHORT).show();
                nombreAlu.setText("");
                apePaAlu.setText("");
                apeMaAlu.setText("");
                boletaAlu.setText("");
                carreraAlu.setText("");
                //mostrarComputadora("2019");

            }
        });
        return view;
    }

    //CONSULTA A LA BASE DE DATOS PARA OBTENER NOMBRE DE ENCARGADO (1)
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
    /*BOTON PARA ESCANEAR QR AQUI OBTENEMOS NOMBRE COMPLETO (debemos separarlo y dividirlo en apellidos)
     obtenemos carrera y obtenemos boleta.
    LO RECOMENDABLE ES AQUI DESPUES DE OBTENER ESOS DATOS REALIZAR UN REGISTRO DEL ALUMNO A LA BASE DE DATOS */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Escaneo Cancelado", Toast.LENGTH_SHORT).show();
            } else {

                try{

                    String url = result.getContents();
                    Log.d("qrContent","Este es el contenido del qr:\n" + url);

                    new WebScrapingTask().execute(url);

                } catch (Exception e){
                    Log.d("Tag","Este es el eror: " + e);
                }
            }
        }
    }


    //CLASE SOLO PARA EL RADIOBUTTON PARA SABER SI ELIGIO EL LAB 1 O LAB 2
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

    private class WebScrapingTask extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... urls){
            String url = urls[0];

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

                Response response = Jsoup.connect(url).execute();
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

            } catch (IOException e){
                Log.d("Error de web scrapping","Este es el eror: " + e);
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);

            /*------------------ DATOS PARA MOSTRAR --------------------*/
            nombreAlu.setText(nombre);
            apePaAlu.setText(apePa);
            apeMaAlu.setText(apeMa);
            boletaAlu.setText(boleta);
            carreraAlu.setText(carrera);
        }
    }

    //CLASE PARA SABER QUE COMPUTADORA SE LE ASIGNO DE AHI MOVERNOS A "ACTUALIZAR COMPUTADORA"
    //PARA PONERLA EN OCUPADA
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
                String sql = "SELECT numero\n" +
                        "FROM COMPUTADORA\n" +
                        "WHERE estado = 1\n" +
                        "AND ROWNUM = 1\n" +
                        "AND ocupada = 0 \n" +
                        "AND laboratorio = ?";
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
                    computadora = resultSet.getString("numero");
                    Log.d("TAG", "Computadora: "+computadora);
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
        protected void onPostExecute(String resultado){
            // Aquí puedes llamar a un método para mostrar la ventana flotante con el resultado
            Log.d("TAG", "On post Computadora: "+ resultado);
            mostrarComputadora(resultado);
            // Una vez que se mostró la computadora, ejecuta la AsyncTask para actualizar a computadora"ocupada"
            ActualizarOcupada actualizarOcupada = new ActualizarOcupada(resultado);
            actualizarOcupada.execute();

        }
    }

    //CLASE PARA PONER LA COMPUTADORA A OCUPADA Y DE AHI MOVERNOS A CREAR SESION YA CON TODOS LOS DATOS
    // Y EL REGISTRO DEL ALUMNO OBTENIDOS
    private class ActualizarOcupada extends AsyncTask<Void, Void, Void>{
        private String numeroDeComputadora;

        public ActualizarOcupada(String numeroDeComputadora) {
            this.numeroDeComputadora = numeroDeComputadora;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            Connection connection = null;
            PreparedStatement statement = null;
            Log.d("TAG", "Computadora en Actualizar: "+ numeroDeComputadora);
            try {
                // Cargar el controlador JDBC de Oracle
                Class.forName(DRIVER);
                // Establecer la conexión a la base de datos
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                // Preparar la consulta SQL para actualizar la columna "ocupada" a 1
                String sql = "UPDATE COMPUTADORA SET ocupada = 1 WHERE numero = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, numeroDeComputadora);

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
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            CrearSesion crearSesion = new CrearSesion();
            crearSesion.execute();
        }
    }
    //CLASE PARA CREAR LA SESION DEL ALUMNO QUE INGRESO
    private class CrearSesion extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

    //ESTE ES SOLO PARA EL MENSAJE DE QUE COMPUTADORA SE LE ASIGNO
    private void mostrarComputadora(String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Computadora Asignada");
        builder.setMessage(mensaje);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Aquí puedes realizar acciones adicionales si el usuario hace clic en Aceptar
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

