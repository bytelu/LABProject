package com.example.labproject.profesor;

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

public class ProfesorAdapter extends RecyclerView.Adapter<ProfesorAdapter.ProfesorViewHolder> {
    ArrayList<Profesor> listaProfesores;
    ArrayList<Profesor> listaOriginal;

    public ProfesorAdapter(ArrayList<Profesor> listaProfesores){
        this.listaProfesores = listaProfesores;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaProfesores);
    }


    @Override
    public int getItemCount(){return listaProfesores.size(); }

    @NonNull
    @Override
    public ProfesorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element_profesores, parent, false);
        return new ProfesorViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ProfesorViewHolder holder, int position){
        holder.name.setText(listaProfesores.get(position).getNOMBRE());
        holder.PLastName.setText(listaProfesores.get(position).getAPELLIDO_P());
        holder.MLastName.setText(listaProfesores.get(position).getAPELLIDO_M());
        holder.Boleta.setText(listaProfesores.get(position).getBOLETA());
    }

    public void filtradoP(String textBuscar){
        int longitud = textBuscar.length();
        if (longitud == 0){
            listaProfesores.clear();
            listaProfesores.addAll(listaOriginal);
        } else {
            String textBusqueda = textBuscar.toLowerCase();
            String[] palabrasAbuscar = textBusqueda.split(" ");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N){
                List<Profesor> collection = listaOriginal.stream()
                        .filter(i -> contienePalabrasP(i, palabrasAbuscar))
                        .collect(Collectors.toList());
                listaProfesores.clear();
                listaProfesores.addAll(collection);
            } else {
                listaProfesores.clear();
                for (Profesor c : listaOriginal) {
                    if (contienePalabrasP(c, palabrasAbuscar)){
                        listaProfesores.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    private boolean contienePalabrasP(Profesor profesor, String[] palabrasAbuscar) {
        String nombreCompleto = profesor.getNOMBRE().toLowerCase() + " " +
        profesor.getAPELLIDO_P().toLowerCase() + " " +
        profesor.getAPELLIDO_M().toLowerCase();

        for (String palabra : palabrasAbuscar) {
            if (!nombreCompleto.contains(palabra)){
                return false;
            }
        }
        return true;
    }

    public void setItems(ArrayList<Profesor> items) { listaProfesores = items; }

    public class ProfesorViewHolder extends RecyclerView.ViewHolder{
        TextView name, PLastName, MLastName, Boleta;

        public ProfesorViewHolder(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.NameTextView);
            PLastName = itemView.findViewById(R.id.PLastNameTextView);
            MLastName = itemView.findViewById(R.id.MLastNameTextView);
            Boleta = itemView.findViewById(R.id.BoletaTextView);
        }
    }
}
