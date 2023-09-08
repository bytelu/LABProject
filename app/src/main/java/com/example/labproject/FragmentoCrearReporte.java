package com.example.labproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Locale;

import com.example.labproject.res.CData;

public class FragmentoCrearReporte extends Fragment {

    private static final String DRIVER = CData.getDriver();
    private static final String URL = CData.getUrl();
    private static final String USERNAME = CData.getUsername();
    private static final String PASSWORD = CData.getPassword();

    private static String laboratorio;

    private TextInputLayout tituloTextInput, computadoraTextInput, descripcionTextInput;

    public FragmentoCrearReporte() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragmento_crear_reporte, container, false);

        tituloTextInput = view.findViewById(R.id.tituloReporteTextInput);
        computadoraTextInput = view.findViewById(R.id.computadoraReporteTextInput);
        descripcionTextInput = view.findViewById(R.id.descripcionReporteTextInput);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        RadioButton radioButton1 = view.findViewById(R.id.laboratorioSelect1);
        RadioButton radioButton2 = view.findViewById(R.id.laboratorioSelect2);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Verifica cuál RadioButton se seleccionó y asigna el valor correspondiente a "laboratorio"
            if (checkedId == R.id.laboratorioSelect1) {
                laboratorio = "1";
            } else if (checkedId == R.id.laboratorioSelect2) {
                laboratorio = "2";
            }
        });

        AppCompatButton button = view.findViewById(R.id.guardarReporteButton);

        button.setOnClickListener(view1 -> {
            String titulo = tituloTextInput.getEditText().getText().toString();
            String computadora = computadoraTextInput.getEditText().getText().toString();
            String descripcion = descripcionTextInput.getEditText().getText().toString();

            if (titulo.isEmpty() || descripcion.isEmpty() || laboratorio.isEmpty()){
                String mensajeError = "Por favor completa los campos:\n";

                if(titulo.isEmpty()){
                    mensajeError += "- Titulo\n";
                }
                if(descripcion.isEmpty()){
                    mensajeError += "- Descripcion\n";
                }
                if(laboratorio.isEmpty()){
                    mensajeError += "- Selecciona un laboratorio\n";
                }

                Toast.makeText(requireContext(), mensajeError, Toast.LENGTH_SHORT).show();

            } else {
                UpdateAsyncTask updateAsyncTask = new UpdateAsyncTask();
                updateAsyncTask.execute(titulo, computadora, descripcion, laboratorio);
            }
        });


        ConstraintLayout fabRegresarReporte = view.findViewById(R.id.regresarReporte);
        fabRegresarReporte.setOnClickListener(view12 -> {

            FragmentoReportes fragmentoDestino = new FragmentoReportes();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragmentoDestino)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }


    private class UpdateAsyncTask extends AsyncTask<String, Void, Boolean>{
        @Override
        protected Boolean doInBackground(String... params){
            Connection connection = null;
            PreparedStatement statement = null;

            try {
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                String titulo = params[0];
                String computadora = params[1];
                String descripcion = params[2];
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                String usuario = sharedPreferences.getString("usuario", "");
                Calendar cal = Calendar.getInstance();
                int hora = cal.get(Calendar.HOUR_OF_DAY);
                int minutos = cal.get(Calendar.MINUTE);
                int segundos = cal.get(Calendar.SECOND);
                int anio = cal.get(Calendar.YEAR);
                int mes = cal.get(Calendar.MONTH) + 1;
                int dia = cal.get(Calendar.DAY_OF_MONTH);
                String horaFormat = String.format(Locale.US,"%04d-%02d-%02d %02d:%02d:%02d", anio, mes, dia, hora, minutos, segundos);
                String fechaFormat = String.format(Locale.US, "%d-%d-%d", anio, mes, dia);

                String sql = "INSERT INTO reporte (TITULO, DESCRIPCION, HORA, FECHA, ENCARGADO_ID, COMPUTADORA_ID) " +
                        "VALUES (?, ?, ?, ?, (SELECT id FROM ENCARGADO WHERE usuario = ?), (SELECT id FROM COMPUTADORA WHERE numero = ? AND laboratorio = ?))";


                statement = connection.prepareStatement(sql);
                statement.setString(1, titulo);
                statement.setString(2, descripcion);
                statement.setString(3, horaFormat);
                statement.setString(4, fechaFormat);
                statement.setString(5, usuario);
                statement.setString(6, computadora);
                statement.setString(7, laboratorio);

                int rowsAffected = statement.executeUpdate();
                return  rowsAffected > 0;
                } catch (Exception e){
                Log.e("Error", "Error en la actualización" + e);
                return false;
            } finally {
                try {
                    if (statement != null){
                        statement.close();
                    }
                    if (connection != null){
                        connection.close();
                    }
                } catch (Exception e){
                    Log.e("Error", "Error en la actualización" + e);
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success){
            Log.d("updateAsyncTask", "onPostExecute iniciado");
            if (success){
                Toast.makeText(requireContext(), "Reporte guardado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Error al guardar reporte", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
