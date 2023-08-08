package com.example.labproject.encargado;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labproject.R;

import java.util.ArrayList;

public class ListaEncargadosAdapte extends RecyclerView.Adapter<ListaEncargadosAdapte.EncargadoViewHolder> {

    ArrayList<encargado> listaEncargados;
    public ListaEncargadosAdapte(ArrayList<encargado> listaEncargados){
        this.listaEncargados = listaEncargados;
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
        holder.encActivo.setText(String.valueOf(listaEncargados.get(position).getEstado()));

    }

    @Override
    public int getItemCount() {
        return listaEncargados.size();
    }

    public class EncargadoViewHolder extends RecyclerView.ViewHolder {

        TextView encNombre, encApeP, encApeM, encEntrada, encSalida, encUsuario, encActivo;
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
