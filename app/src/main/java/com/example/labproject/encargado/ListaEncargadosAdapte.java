package com.example.labproject.encargado;

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

public class ListaEncargadosAdapte extends RecyclerView.Adapter<ListaEncargadosAdapte.EncargadoViewHolder> {

    ArrayList<encargado> listaEncargados;
    ArrayList<encargado> listaOriginal;
    public ListaEncargadosAdapte(ArrayList<encargado> listaEncargados){
        this.listaEncargados = listaEncargados;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaEncargados);
    }

    @NonNull
    @Override
    public EncargadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listaencargados, parent, false);
        return new EncargadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EncargadoViewHolder holder, int position) {
        holder.encNombre.setText(listaEncargados.get(position).getNombre());
        holder.encApeP.setText(listaEncargados.get(position).getApellido_p());
        holder.encApeM.setText(listaEncargados.get(position).getApellido_m());
        holder.encEntrada.setText(listaEncargados.get(position).getHora_entrada());
        holder.encSalida.setText(listaEncargados.get(position).getHora_salida());
        holder.encUsuario.setText(listaEncargados.get(position).getUsuario());

        // Obtener el valor del laboratorio en la posición actual
        int estadoEnc = listaEncargados.get(position).getEstado();

        // Obtener el ImageView para la imagen del laboratorio
        ImageView imgActivo = holder.itemView.findViewById(R.id.encActivo);
        // Determinar qué imagen mostrar según el valor del laboratorio
        if (estadoEnc == 1) {
            imgActivo.setImageResource(R.drawable.encendido);
        } else if (estadoEnc == 0) {
            imgActivo.setImageResource(R.drawable.apagado);
        } else {
            imgActivo.setImageResource(R.drawable.iclogoo);
        }
    }

    public void filtrado(String txtBuscar){
        int longitud = txtBuscar.length();
        if( longitud == 0){
            listaEncargados.clear();
            listaEncargados.addAll(listaOriginal);
        } else {
            String textoBusqueda = txtBuscar.toLowerCase(); // Convertir el texto de búsqueda a minúsculas
            String[] palabrasBusqueda = textoBusqueda.split(" "); // Dividir el texto en palabras

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<encargado> collecion = listaOriginal.stream()
                        .filter(i -> contienePalabras(i, palabrasBusqueda))
                        .collect(Collectors.toList());
                listaEncargados.clear();
                listaEncargados.addAll(collecion);
            } else {
                listaEncargados.clear();
                for (encargado c: listaOriginal){
                    if (contienePalabras(c, palabrasBusqueda)) {
                        listaEncargados.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    private boolean contienePalabras(encargado encargado, String[] palabrasBusqueda) {
        String nombreCompleto = encargado.getNombre().toLowerCase() + " " +
                encargado.getApellido_p().toLowerCase() + " " +
                encargado.getApellido_m().toLowerCase();

        for (String palabra : palabrasBusqueda) {
            if (!nombreCompleto.contains(palabra)) {
                return false; // Si alguna palabra no coincide, se retorna false
            }
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return listaEncargados.size();
    }

    public class EncargadoViewHolder extends RecyclerView.ViewHolder {

        TextView encNombre, encApeP, encApeM, encEntrada, encSalida, encUsuario;
        ImageView encActivo;
        public EncargadoViewHolder(@NonNull View itemView) {
            super(itemView);
            encNombre = itemView.findViewById(R.id.encNombre);
            encApeP = itemView.findViewById(R.id.encApeP);
            encApeM = itemView.findViewById(R.id.encApeM);
            encEntrada = itemView.findViewById(R.id.encEntrada);
            encSalida = itemView.findViewById(R.id.encSalida);
            encUsuario = itemView.findViewById(R.id.encUsuario);
            encActivo = itemView.findViewById(R.id.encActivo);
        }
    }
}
