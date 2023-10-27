package com.example.labproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.labproject.res.CData;
import com.example.labproject.sesiongrupallab1.ListaSesionGrupalLabUnoAdapter;
import com.example.labproject.sesiongrupallab1.SesionGrupalLaboratorioUno;
import com.google.android.material.button.MaterialButton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class FragmentoLabUno extends Fragment implements SearchView.OnQueryTextListener {

    /*Conexion con BD*/
    private static final String DRIVER = CData.getDriver();
    private static final String URL = CData.getUrl();
    private static final String USERNAME = CData.getUsername();
    private static final String PASSWORD = CData.getPassword();
    TextView txtNombreEnc,txtEncApeP,txtEncApeM, txtNombreProf, txtProfApeP, txtProfApeM, txtFecha, txtEntrada, txtSalida;
    MaterialButton finalizar;
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
        //VARIABLES MUY REPETIDAS
        txtNombreEnc = view.findViewById(R.id.encargadoNombreLab1);
        txtEncApeP = view.findViewById(R.id.encargadoApePLab1);
        txtEncApeM = view.findViewById(R.id.encargadoApeMLab1);
        txtNombreProf = view.findViewById(R.id.profNombreLab1);
        txtProfApeP = view.findViewById(R.id.profApePLab1);
        txtProfApeM = view.findViewById(R.id.profApeMLab1);
        txtFecha = view.findViewById(R.id.fecha);
        txtEntrada = view.findViewById(R.id.lab1Entrada);
        txtSalida = view.findViewById(R.id.lab1Salida);
        finalizar = view.findViewById(R.id.finalizarGrupal);
        //referencia del buscar
        txtBuscar = view.findViewById(R.id.txtBuscar);
        txtBuscar.setQueryHint("Búsqueda por Alumno");

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

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FinalizarSesionTask().execute();
            }
        });

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
                        "    DATE_FORMAT(sesion.fecha, '%d-%m-%Y') AS fecha,\n" +
                        "    DATE_FORMAT(sesion.hora_inicio, '%H:%i') AS hora_inicio,\n" +
                        "    DATE_FORMAT(sesion.hora_final, '%H:%i') AS hora_final,\n" +
                        "    encargado.nombre AS nombre_encargado,\n" +
                        "    encargado.apellido_p AS encargado_apellido_p,\n" +
                        "    encargado.apellido_m AS encargado_apellido_m,\n" +
                        "    computadora.numero AS numero_computadora,\n" +
                        "    computadora.laboratorio AS laboratorio_computadora,\n" +
                        "    estudiante.nombre AS nombre_alumno,\n" +
                        "    estudiante.apellido_p AS alumno_apellido_p,\n" +
                        "    estudiante.apellido_m AS alumno_apellido_m,\n" +
                        "    estudiante.boleta AS no_boleta,\n" +
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
                while (resultSet.next()){
                    SesionGrupalLaboratorioUno sesGruLabUno = new SesionGrupalLaboratorioUno();
                    //VARIABLES REPETIDAS
                    sesGruLabUno.setNombreEnc(resultSet.getString("nombre_encargado"));
                    sesGruLabUno.setEncApeP(resultSet.getString("encargado_apellido_p"));
                    sesGruLabUno.setEncApeM(resultSet.getString("encargado_apellido_m"));
                    sesGruLabUno.setNombreProf(resultSet.getString("nombre_profesor"));
                    sesGruLabUno.setProfApeP(resultSet.getString("profesor_apellido_p"));
                    sesGruLabUno.setProfApeM(resultSet.getString("profesor_apellido_m"));
                    sesGruLabUno.setFecha(resultSet.getString("fecha"));
                    sesGruLabUno.setEntrada(resultSet.getString("hora_inicio"));
                    sesGruLabUno.setSalida(resultSet.getString("hora_final"));
                    //VARIABLES DIFERENTES
                    sesGruLabUno.setComputadora(Integer.toString(resultSet.getInt("numero_computadora")));
                    sesGruLabUno.setNoBoleta(Integer.toString(resultSet.getInt("no_boleta")));// Convertir int a String
                    sesGruLabUno.setAluNombre(resultSet.getString("nombre_alumno"));
                    sesGruLabUno.setAluApeP(resultSet.getString("alumno_apellido_p"));
                    sesGruLabUno.setAluApeM(resultSet.getString("alumno_apellido_m"));

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
            if (!lista.isEmpty()) {
                SesionGrupalLaboratorioUno primerRegistro = lista.get(0);

                String nombreEnc = primerRegistro.getNombreEnc();
                String encApeP = primerRegistro.getEncApeP();
                String encApeM = primerRegistro.getEncApeM();
                String nombreProf = primerRegistro.getNombreProf() ;
                String profApeP = primerRegistro.getProfApeP() ;
                String profApeM = primerRegistro.getProfApeM();
                String fecha = primerRegistro.getFecha();
                String entrada = primerRegistro.getEntrada();
                String salida = primerRegistro.getSalida();

                txtNombreEnc.setText(nombreEnc);
                txtEncApeP.setText(encApeP);
                txtEncApeM.setText(encApeM);
                txtNombreProf.setText(nombreProf);
                txtProfApeP.setText(profApeP);
                txtProfApeM.setText(profApeM);
                txtFecha.setText(fecha);
                txtEntrada.setText(entrada);
                txtSalida.setText(salida);
            }

            adapter = new ListaSesionGrupalLabUnoAdapter(lista);
            listaLaboratorioUnoGrupal.setAdapter(adapter);
        }
    }
    private class FinalizarSesionTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            Connection connection = null;
            PreparedStatement statement = null;

            try {
                // Cargar el controlador JDBC de Oracle
                Class.forName(DRIVER);
                // Establecer la conexión a la base de datos
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                String consulta = "UPDATE computadora\n" +
                        "SET ocupada = 0\n" +
                        "WHERE ocupada = 1 AND laboratorio = 1;";
                statement = connection.prepareStatement(consulta);
                statement.executeUpdate();
                Log.e("Computadoras", "Actualizadas");

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

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Laboratorio laboratorio = new Laboratorio();
            laboratorio.execute();
        }
    }
    private class Laboratorio extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            Connection connection = null;
            PreparedStatement statement = null;

            try {
                // Cargar el controlador JDBC de Oracle
                Class.forName(DRIVER);
                // Establecer la conexión a la base de datos
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String horaActual = timeFormat.format(new Date());

                // Crea y ejecuta la consulta SQL para actualizar la hora final
                String sql = "UPDATE sesion\n" +
                        "INNER JOIN computadora ON sesion.computadora_id = computadora.id\n" +
                        "SET sesion.hora_final = ?, sesion.activo = 0\n" +
                        "WHERE sesion.activo = 1 AND computadora.laboratorio = 1";
                statement = connection.prepareStatement(sql);
                statement.setString(1, horaActual);

                // Ejecuta la consulta de actualización
                statement.executeUpdate();
                Log.e("Computadoras", "Actualizadas");

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

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            mostrarConfirmacion();
        }
    }
    private void mostrarConfirmacion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Sesión Grupal Finalizada");
        builder.setMessage("La sesión grupal del Laboratorio 1 ha sido finalizada con éxito.");

        // Personaliza el estilo del diálogo (opcional)
        builder.setIcon(R.drawable.esimefoto); // Agrega un ícono
        builder.setCancelable(false); // Evita que el diálogo se cierre al tocar fuera de él

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Acciones a realizar al hacer clic en "Aceptar"
                dialog.dismiss(); // Cierra el diálogo
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}