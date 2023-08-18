package com.example.labproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class FragmentoCrearIndividual extends Fragment {

    TextView nombreEnc, apePaEnc, apeMaEnc;
    TextInputLayout horaEntrada, fechaEntrada;
    MaterialButton escanear;

    public FragmentoCrearIndividual() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                //Aqui se manda a llamar a la camara y todo lo de web scraping
            }
        });

        return view;
    }
}