package com.example.labproject.reportes;

import android.app.MediaRouteButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    public ReporteViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vistareportes, parent, false);
        return new ReporteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReporteViewHolder holder, int position) {
        holder.flechadesplegable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean contenidoExpandido = false;
                if (contenidoExpandido) {
                    holder.layoutContenido.setVisibility(View.GONE);
                    contenidoExpandido = false;
                } else {
                    holder.layoutContenido.setVisibility(View.VISIBLE);
                    contenidoExpandido = true;
                }
            }
        }
    }


    public void filtrado(String textBuscarRep) {
        int longitud = textBuscarRep.length();
        if (longitud == 0) {
            listareportes.clear();
            listareportes.addAll(listaOriginal);
        } else {
            String textbuscar = textBuscarRep.toLowerCase();
            String[] palabrasabuscar = textbuscar.split(" ");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Reporte> collecion = listaOriginal.stream()
                        .filter(i -> contienePalabras(i, palabrasabuscar))
                        .collect(Collectors.toList());
                listareportes.clear();
                listareportes.addAll(collecion);
            } else {
                listareportes.clear();
                for (Reporte c : listaOriginal) {
                    if (contienePalabras(c,palabrasabuscar)){
                        listareportes.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    private boolean contienePalabras(Reporte Reporte, String[] palabrasabuscar) {
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
    TextView Textfecha, Texthora, EncFecHora, ViewTitulo, TextNombreEnc, ViewNombreEncRep, ViewApePEncRep, ViewApeMEncRep, TextViewNumComputadora, ViewNumRep, ViewDescripcion;
    ImageView flechadesplegable, VistaNumeroCompu, ImagenReportes;
    LinearLayout layoutcontenido;
    boolean contenidoExpandido = false;
    public class ReporteViewHolder extends RecyclerView.ViewHolder {
        public View flechadesplegable;
        public MediaRouteButton layoutContenido;

        public ReporteViewHolder(View view) {
            super(view);
            Textfecha = itemView.findViewById(R.id.Textfecha);
            Texthora = itemView.findViewById(R.id.Texthora);
            EncFecHora = itemView.findViewById(R.id.EncFecHora);
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
            VistaNumeroCompu = itemView.findViewById(R.id.VistaNumeroCompu);
            ImagenReportes = itemView.findViewById(R.id.ImagenReportes);

            flechadesplegable = itemView.findViewById(R.id.flechadesplegable);
            layoutContenido = itemView.findViewById(R.id.DesplegableVistaRep);

        }
    }
    public void toggleTextVisibility(View view) {
        if (TextViewNumComputadora.getVisibility() == View.VISIBLE) {
            TextViewNumComputadora.setVisibility(View.GONE);
            flechadesplegable.setImageResource(R.drawable.flechadesplegable); // Cambiar la imagen de flecha a "abajo"
        } else {
            TextViewNumComputadora.setVisibility(View.VISIBLE);
            flechadesplegable.setImageResource(R.drawable.flechadesplegable); // Cambiar la imagen de flecha a "arriba"
        }
    }
}
