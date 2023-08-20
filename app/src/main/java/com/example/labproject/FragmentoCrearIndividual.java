package com.example.labproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class FragmentoCrearIndividual extends Fragment {

    TextView nombreEnc, apePaEnc, apeMaEnc;
    TextInputLayout horaEntrada, fechaEntrada;
    MaterialButton escanear;

    public FragmentoCrearIndividual() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragmento_crear_individual, container, false);
        // Asignando variables al encargado
        nombreEnc = view.findViewById(R.id.encNombre);
        apePaEnc = view.findViewById(R.id.encApeP);
        apeMaEnc = view.findViewById(R.id.encApeM);
        // Asignando Variables a fecha y hora
        horaEntrada = view.findViewById(R.id.horaEntradaTextView);
        fechaEntrada = view.findViewById(R.id.fechaEntradaTextView);
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

}

