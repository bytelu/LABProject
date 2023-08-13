package com.example.labproject.profesor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labproject.R;

import java.util.ArrayList;

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
    public ProfesorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element_profesores, parent, false);
        return new ProfesorViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ProfesorViewHolder holder, int position){
        holder.name.setText(listaProfesores.get(position).getNOMBRE());
        holder.PLastName.setText(listaProfesores.get(position).getAPELLIDO_P());
        holder.MLastName.setText(listaProfesores.get(position).getAPELLIDO_M());
        holder.Boleta.setText(listaProfesores.get(position).getBOLETA());
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
