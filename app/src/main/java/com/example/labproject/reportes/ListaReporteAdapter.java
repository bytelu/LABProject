package com.example.labproject.reportes;

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

public class ListaReporteAdapter extends RecyclerView.Adapter<ListaReporteAdapter.ReporteViewHolder> {
    ArrayList<Reporte> listareportes;
    ArrayList<Reporte> listaOriginal;
    public ListaReporteAdapter(ArrayList<Reporte> listareportes) {
        this.listareportes = listareportes;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listareportes);
    }

    @NonNull
    @Override
    public ReporteViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listareporte, parent, false);
        return new ReporteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReporteViewHolder holder, int position) {
        holder.ViewTitulo.setText(listareportes.get(position).getTITULO());
        holder.Textfecha.setText(listareportes.get(position).getFECHA());
        holder.Texthora.setText(listareportes.get(position).getHORA());
    }


    public void filtradoR(String textBuscarRep) {
        int longitud = textBuscarRep.length();
        if (longitud == 0) {
            listareportes.clear();
            listareportes.addAll(listaOriginal);
        } else {
            String textbuscar = textBuscarRep.toLowerCase();
            String[] palabrasabuscar = textbuscar.split(" ");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Reporte> collecion = listaOriginal.stream()
                        .filter(i -> contienePalabrasR(i, palabrasabuscar))
                        .collect(Collectors.toList());
                listareportes.clear();
                listareportes.addAll(collecion);
            } else {
                listareportes.clear();
                for (Reporte c : listaOriginal) {
                    if (contienePalabrasR(c,palabrasabuscar)){
                        listareportes.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    private boolean contienePalabrasR(Reporte Reporte, String[] palabrasabuscar) {
        return true;
    }

    @Override
    public int getItemCount() {return listareportes.size();}
    public class ReporteViewHolder extends RecyclerView.ViewHolder {
        TextView Textfecha, Texthora,ViewTitulo;

        public ReporteViewHolder(View view) {
            super(view);
            Textfecha = view.findViewById(R.id.fechaReporte);
            Texthora = view.findViewById(R.id.horaReporte);
            ViewTitulo = view.findViewById(R.id.tituloReporte);
        }
    }
}

