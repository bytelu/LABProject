package com.example.labproject;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentoCrearReporte extends Fragment {

    public FragmentoCrearReporte() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragmento_crear_reporte, container, false);

        ConstraintLayout fabRegresarReporte = view.findViewById(R.id.regresarReporte);

        fabRegresarReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reemplaza "FragmentoDestino" con el nombre de tu fragmento de destino
                FragmentoReportes fragmentoDestino = new FragmentoReportes();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragmentoDestino) // Reemplaza fragment_container con el ID de tu contenedor de fragmentos
                        .addToBackStack(null) // Opcional: agrega la transacción a la pila de retroceso
                        .commit();
            }
        });

        return view;
    }
}