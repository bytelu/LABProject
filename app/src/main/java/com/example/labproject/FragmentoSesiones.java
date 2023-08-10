package com.example.labproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.labproject.sesindividual.SesionIndividual;

public class FragmentoSesiones extends Fragment {
    private boolean esRegistro = false; // Variable para controlar qué fragmento se muestra

    public FragmentoSesiones() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_fragmento_sesiones, container, false);

        // Referencias a las pestañas
        TextView tabSesionIndividual = view.findViewById(R.id.tabSesionIndividual);
        TextView tabSesionGrupal = view.findViewById(R.id.tabSesionGrupal);

        tabSesionIndividual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarFragmento(new FragmentoCrearIndividual());
            }
        });

        tabSesionGrupal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarFragmento(new FragmentoCrearGrupal());
            }
        });

        if(savedInstanceState == null){
            cambiarFragmento(new FragmentoCrearGrupal());
        }
        return view;
    }

    //Metodo para cambiar fragmentos
    private void cambiarFragmento(Fragment fragment){
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragmentContenedor, fragment)
                .commit();
    }
}