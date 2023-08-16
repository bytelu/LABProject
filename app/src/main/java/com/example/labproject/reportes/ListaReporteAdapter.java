package com.example.labproject.reportes;


import android.app.MediaRouteButton;
import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labproject.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import androidx.appcompat.app.AppCompatActivity;
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
    public ReporteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType ) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vistareportes, parent, false);
        LinearLayout motherlayout = view.findViewById(R.id.motherlayout);
        LinearLayout fechahora = view.findViewById(R.id.fechahora);
        LinearLayout titleflechadesp = view.findViewById(R.id.titleflechadesp);
        RelativeLayout DesplegableVistaRep = view.findViewById(R.id.DesplegableVistaRep);
        ImageView flechadesplegable = view.findViewById(R.id.flechadesplegable);
        ImageView VistaNumeroCompu = view.findViewById(R.id.VistaNumeroCompu);
        ImageView ImagenReportes = view.findViewById(R.id.ImagenReportes);

        flechadesplegable.setOnClickListener(view1 -> {
            if(DesplegableVistaRep.getVisibility() == View.GONE){
                TransitionManager.beginDelayedTransition(motherlayout, new AutoTransition());
                DesplegableVistaRep.setVisibility(View.VISIBLE);
            }
        });

        return new ReporteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReporteViewHolder holder, int position) {

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
        String nombreCompleto = Reporte.getNOMBRE().toLowerCase() + " " +
                Reporte.getAPELLIDO_P().toLowerCase() + " " +
                Reporte.getAPELLIDO_M().toLowerCase();

        for (String palab : palabrasabuscar)
            if (!nombreCompleto.contains(palab)) {
                return false; // Si alguna palabra no coincide, se retorna false
            }
        return true;
    }

    @Override
    public int getItemCount() {return listareportes.size();}
    public class ReporteViewHolder extends RecyclerView.ViewHolder {
        TextView Textfecha, Texthora, EncFecHora, ViewTitulo, TextNombreEnc, ViewNombreEncRep, ViewApePEncRep, ViewApeMEncRep, TextViewNumComputadora, ViewNumRep, ViewDescripcion;
        ImageView flechadesplegable, VistaNumeroCompu, ImagenReportes;
        boolean contenidoExpandido = false;
        //public View flechadesplegable;
        public MediaRouteButton layoutContenido;

        public ReporteViewHolder(View view) {
            super(view);
            Textfecha = itemView.findViewById(R.id.Textfecha);
            Texthora = itemView.findViewById(R.id.Texthora);
            ViewTitulo = itemView.findViewById(R.id.ViewTitulo);
            TextNombreEnc = itemView.findViewById(R.id.TextNombreEnc);
            ViewNombreEncRep = itemView.findViewById(R.id.ViewNombreEncRep);
            ViewApePEncRep = itemView.findViewById(R.id.ViewApePEncRep);
            ViewApePEncRep = itemView.findViewById(R.id.ViewApePEncRep);
            ViewApeMEncRep = itemView.findViewById(R.id.ViewApeMEncRep);
            TextViewNumComputadora = itemView.findViewById(R.id.TextViewNumComputadora);
            ViewNumRep = itemView.findViewById(R.id.ViewNumRep);
            ViewDescripcion = itemView.findViewById(R.id.ViewDescripcion);
            flechadesplegable = itemView.findViewById(R.id.flechadesplegable);
        }
    }
}
