package com.example.labproject.alumnos;

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

public class ListaAlumnoAdapter extends RecyclerView.Adapter<ListaAlumnoAdapter.AlumnoViewHolder>{
    ArrayList<Alumno> listaalumnos;
    ArrayList<Alumno> listaOriginal;
    public ListaAlumnoAdapter(ArrayList<Alumno> listaalumnos){
        this.listaalumnos = listaalumnos;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaalumnos);
    }

    @NonNull
    @Override
    public AlumnoViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_alumnos, parent , false);
        return new AlumnoViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull AlumnoViewHolder holder, int position) {
        holder.aluNombre.setText(listaalumnos.get(position).getNOMBRE());
        holder.aluApeP.setText(listaalumnos.get(position).getAPELLIDO_P());
        holder.aluApeM.setText(listaalumnos.get(position).getAPELLIDO_M());
        holder.aluBoleta.setText(String.valueOf(listaalumnos.get(position).getBOLETA()));
        holder.aluCarrera.setText(listaalumnos.get(position).getCARRERA());
        holder.aluSemestre.setText(String.valueOf(listaalumnos.get(position).getSEMESTRE()));
        //holder.aluComp.setText(listaalumnos.get(position).//cómo ponerle para °;

    }

    public void filtrado(String textBuscarAlum) {
        int longitud = textBuscarAlum.length();
        if (longitud == 0) {
            listaalumnos.clear();
            listaalumnos.addAll(listaOriginal);
        } else {
            String textobusq = textBuscarAlum.toLowerCase();
            String[] palabrasbuscar = textobusq.split(" ");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Alumno> collecion = listaOriginal.stream()
                        .filter(i -> contienePalabras(i, palabrasbuscar))
                        .collect(Collectors.toList());
                listaalumnos.clear();
                listaalumnos.addAll(collecion);
            } else {
                listaalumnos.clear();
                for (Alumno c : listaOriginal) {
                    if (contienePalabras(c,palabrasbuscar)){
                        listaalumnos.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    private boolean contienePalabras(Alumno Alumno, String [] palabrasbuscar) {
        String nombreCompleto = Alumno.getNOMBRE().toLowerCase() + " " +
                Alumno.getAPELLIDO_P().toLowerCase() + " " +
                Alumno.getAPELLIDO_M().toLowerCase();

        for (String palab : palabrasbuscar)
            if (!nombreCompleto.contains(palab)) {
                return false; // Si alguna palabra no coincide, se retorna false
            }
        return true;
    }

    @Override
    public int getItemCount() {
        return listaalumnos.size();
    }


    public class AlumnoViewHolder extends RecyclerView.ViewHolder{
        TextView aluNombre, aluApeP, aluApeM, aluBoleta, aluCarrera, aluSemestre, aluComp;

        public AlumnoViewHolder(@NonNull View itemView){
            super(itemView);
            aluNombre = itemView.findViewById(R.id.aluNombre);
            aluApeP = itemView.findViewById(R.id.aluApeP);
            aluApeM = itemView.findViewById(R.id.aluApeM);
            aluBoleta = itemView.findViewById(R.id.aluBoleta);
            aluCarrera = itemView.findViewById(R.id.aluCarrera);
            aluSemestre = itemView.findViewById(R.id.aluSemestre);
            aluComp = itemView.findViewById(R.id.aluComp);

        }

    }
}




