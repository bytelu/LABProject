package com.example.labproject.sesiongrupallab1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labproject.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaSesionGrupalLabUnoAdapter extends RecyclerView.Adapter<ListaSesionGrupalLabUnoAdapter.ListaSesionGrupalLabUnoViewHolder> {
    ArrayList<SesionGrupalLaboratorioUno> listaSesionGrupalLabUno;
    ArrayList<SesionGrupalLaboratorioUno> listaOriginal;

    public ListaSesionGrupalLabUnoAdapter(ArrayList<SesionGrupalLaboratorioUno> listaSesionGrupalLabUno){
        this.listaSesionGrupalLabUno = listaSesionGrupalLabUno;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaSesionGrupalLabUno);
    }
    @NonNull
    @Override
    public ListaSesionGrupalLabUnoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listasesiongrupallabuno,parent,false);
        return new ListaSesionGrupalLabUnoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaSesionGrupalLabUnoViewHolder holder, int position) {
        holder.computadora.setText(listaSesionGrupalLabUno.get(position).getComputadora());
        holder.noBoleta.setText(listaSesionGrupalLabUno.get(position).getNoBoleta());
        holder.aluNombre.setText(listaSesionGrupalLabUno.get(position).getAluNombre());
        holder.aluApeP.setText(listaSesionGrupalLabUno.get(position).getAluApeP());
        holder.aluApeM.setText(listaSesionGrupalLabUno.get(position).getAluApeM());
        holder.semestre.setText(listaSesionGrupalLabUno.get(position).getSemestreAlu());
    }

    public void filtrado(String txtBuscar){
        int longitud = txtBuscar.length();
        if (longitud == 0) {
            listaSesionGrupalLabUno.clear();
            listaSesionGrupalLabUno.addAll(listaOriginal);
        } else {
            String textoBusqueda = txtBuscar.toLowerCase();
            String[] palabrasBusqueda = textoBusqueda.split(" ");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<SesionGrupalLaboratorioUno> collecion = listaOriginal.stream()
                        .filter(i -> contienePalabras(i, palabrasBusqueda))
                        .collect(Collectors.toList());
                listaSesionGrupalLabUno.clear();
                listaSesionGrupalLabUno.addAll(collecion);
            } else {
                listaSesionGrupalLabUno.clear();
                for (SesionGrupalLaboratorioUno c : listaOriginal) {
                    if (contienePalabras(c, palabrasBusqueda)) {
                        listaSesionGrupalLabUno.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    private boolean contienePalabras(SesionGrupalLaboratorioUno sesIndv, String[] palabrasBusqueda) {
        String nombreCompleto = sesIndv.getAluNombre().toLowerCase() + " " +
                sesIndv.getAluApeP().toLowerCase() + " " +
                sesIndv.getAluApeM().toLowerCase();

        for (String palabra : palabrasBusqueda) {
            if (!nombreCompleto.contains(palabra)) {
                return false; // Si alguna palabra no coincide, se retorna false
            }
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return listaSesionGrupalLabUno.size();
    }

    public class ListaSesionGrupalLabUnoViewHolder extends RecyclerView.ViewHolder {
        TextView computadora,noBoleta,semestre, aluNombre,aluApeP,aluApeM;
        public ListaSesionGrupalLabUnoViewHolder(@NonNull View itemView) {
            super(itemView);
            computadora = itemView.findViewById(R.id.aluComputadoraLab1);
            noBoleta = itemView.findViewById(R.id.aluBoletaLab1);
            semestre = itemView.findViewById(R.id.aluSemestreLab1);
            aluNombre = itemView.findViewById(R.id.aluNombreLab1);
            aluApeP = itemView.findViewById(R.id.aluApePLab1);
            aluApeM = itemView.findViewById(R.id.aluApeMLab1);
        }
    }
}
