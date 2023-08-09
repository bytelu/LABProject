package com.example.labproject.sesindividual;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labproject.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaSesionIndividualAdapter extends RecyclerView.Adapter<ListaSesionIndividualAdapter.ListaSesionIndividualViewHolder> {
    ArrayList<SesionIndividual> listaSesionIndividual;
    ArrayList<SesionIndividual> listaOriginal;

    public ListaSesionIndividualAdapter(ArrayList<SesionIndividual> listaSesionIndividual){
        this.listaSesionIndividual = listaSesionIndividual;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaSesionIndividual);
    }

    @NonNull
    @Override
    public ListaSesionIndividualViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listasesionindividual, parent, false);
        return new ListaSesionIndividualViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaSesionIndividualViewHolder holder, int position) {
        holder.fecha.setText(listaSesionIndividual.get(position).getFecha());
        holder.computadora.setText(listaSesionIndividual.get(position).getComputadora());

        // Obtener el valor del laboratorio en la posición actual
        String laboratorio = listaSesionIndividual.get(position).getLaboratorio();

        // Obtener el ImageView para la imagen del laboratorio
        ImageView imgLaboratorio = holder.itemView.findViewById(R.id.imagenLaboratorio);

        // Determinar qué imagen mostrar según el valor del laboratorio
        if (laboratorio.equals("1")) {
            imgLaboratorio.setImageResource(R.drawable.laboratorio1);
        } else if (laboratorio.equals("2")) {
            imgLaboratorio.setImageResource(R.drawable.laboratorio2);
        } else {
            imgLaboratorio.setImageResource(R.drawable.iclogoo);
        }

        holder.aluNombre.setText(listaSesionIndividual.get(position).getAluNombre());
        holder.aluApeP.setText(listaSesionIndividual.get(position).getAluApeP());
        holder.aluApeM.setText(listaSesionIndividual.get(position).getAluApeM());
        holder.encNombre.setText(listaSesionIndividual.get(position).getEncNombre());
        holder.encApeP.setText(listaSesionIndividual.get(position).getEncApeP());
        holder.encApeM.setText(listaSesionIndividual.get(position).getEncApeM());
        holder.sesEntrada.setText(listaSesionIndividual.get(position).getSesEntrada());
        holder.sesSalida.setText(listaSesionIndividual.get(position).getSesSalida());
    }

    public void filtrado(String txtBuscar){
        int longitud = txtBuscar.length();
        if (longitud == 0) {
            listaSesionIndividual.clear();
            listaSesionIndividual.addAll(listaOriginal);
        } else {
            String textoBusqueda = txtBuscar.toLowerCase();
            String[] palabrasBusqueda = textoBusqueda.split(" ");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<SesionIndividual> collecion = listaOriginal.stream()
                        .filter(i -> contienePalabras(i, palabrasBusqueda))
                        .collect(Collectors.toList());
                listaSesionIndividual.clear();
                listaSesionIndividual.addAll(collecion);
            } else {
                listaSesionIndividual.clear();
                for (SesionIndividual c : listaOriginal) {
                    if (contienePalabras(c, palabrasBusqueda)) {
                        listaSesionIndividual.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    private boolean contienePalabras(SesionIndividual sesIndv, String[] palabrasBusqueda) {
        String nombreCompleto = sesIndv.getAluNombre().toLowerCase() + " " +
                sesIndv.getAluApeP().toLowerCase() + " " +
                sesIndv.getAluApeM().toLowerCase();

        String fechaSesion = sesIndv.getFecha(); // Obtener la fecha formateada de la sesión

        for (String palabra : palabrasBusqueda) {
            if (!nombreCompleto.contains(palabra) && !fechaSesion.contains(palabra)) {
                return false; // Si alguna palabra no coincide, se retorna false
            }
        }
        return true;
    }
    @Override
    public int getItemCount() {
        return listaSesionIndividual.size();
    }

    public class ListaSesionIndividualViewHolder extends RecyclerView.ViewHolder {
        TextView fecha,computadora,aluNombre,aluApeP,aluApeM,encNombre,encApeP,encApeM,sesEntrada,sesSalida;
        ImageView laboratorioComputadora;
        public ListaSesionIndividualViewHolder(@NonNull View itemView) {
            super(itemView);
            fecha = itemView.findViewById(R.id.fecha);
            computadora = itemView.findViewById(R.id.computadora);
            laboratorioComputadora = itemView.findViewById(R.id.imagenLaboratorio);
            aluNombre= itemView.findViewById(R.id.aluNombre);
            aluApeP= itemView.findViewById(R.id.aluApeP);
            aluApeM = itemView.findViewById(R.id.aluApeM);
            encNombre = itemView.findViewById(R.id.encNombre);
            encApeP = itemView.findViewById(R.id.encApeP);
            encApeM = itemView.findViewById(R.id.encApeM);
            sesEntrada = itemView.findViewById(R.id.sesEntrada);
            sesSalida = itemView.findViewById(R.id.sesSalida);
        }
    }
}
