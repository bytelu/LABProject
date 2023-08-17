package com.example.labproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentoSesionGrupal extends Fragment {
    private boolean esRegistro = false; // Variable para controlar qué fragmento se muestra

    public FragmentoSesionGrupal() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragmento_sesion_grupal, container, false);

        // Referencias a las pestañas
        TextView tabLaboratorio1 = view.findViewById(R.id.tabLaboratorioUno);
        TextView tabLaboratorio2 = view.findViewById(R.id.tabLaboratorioDos);

        tabLaboratorio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarFragmento(new FragmentoLabUno());
            }
        });

        tabLaboratorio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarFragmento(new FragmentoLabDos());
            }
        });

        if(savedInstanceState == null){
            cambiarFragmento(new FragmentoLabUno());
        }
        return view;
    }

    //Metodo para cambiar fragmentos
    private void cambiarFragmento(Fragment fragment){
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragmentContLabs, fragment)
                .commit();
    }
}