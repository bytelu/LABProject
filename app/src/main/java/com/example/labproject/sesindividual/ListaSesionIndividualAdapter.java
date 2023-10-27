package com.example.labproject.sesindividual;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labproject.FragmentoSesionIndividual;
import com.example.labproject.R;
import com.google.android.material.button.MaterialButton;
import com.example.labproject.FragmentoSesionIndividual; // Asegúrate de que la ruta sea la correcta

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaSesionIndividualAdapter extends RecyclerView.Adapter<ListaSesionIndividualAdapter.ListaSesionIndividualViewHolder> {
    ArrayList<SesionIndividual> listaSesionIndividual;
    ArrayList<SesionIndividual> listaOriginal;
    private FragmentoSesionIndividual fragmentoSesionIndividual;

    public ListaSesionIndividualAdapter(ArrayList<SesionIndividual> listaSesionIndividual){
        this.listaSesionIndividual = listaSesionIndividual;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaSesionIndividual);
        fragmentoSesionIndividual = new FragmentoSesionIndividual();
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
        holder.idSesion.setText(listaSesionIndividual.get(position).getIdSesion());
        holder.finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = holder.idSesion.getText().toString();
                // Puedes mostrar el ID de la sesión (por ejemplo, en un Toast)
                // Llama al AsyncTask para realizar la actualización en segundo plano
                if(fragmentoSesionIndividual != null) {
                    FragmentoSesionIndividual.ActualizarSesionAsyncTask task = fragmentoSesionIndividual.new ActualizarSesionAsyncTask();
                    task.execute(id);
                }
            }
        });
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
        TextView fecha,computadora,aluNombre,aluApeP,aluApeM,encNombre,encApeP,encApeM,sesEntrada,idSesion;
        ImageView laboratorioComputadora;
        MaterialButton finalizar;
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
            idSesion = itemView.findViewById(R.id.idSesion);

            finalizar = itemView.findViewById(R.id.finalizarIndividual);
        }
    }
    private void showSuccessMessage() {
        // Muestra un mensaje de éxito aquí
        Toast.makeText(fragmentoSesionIndividual.getContext(), "Sesión finalizada con éxito", Toast.LENGTH_SHORT).show();
    }
    private void showErrorMessage() {
        // Muestra un mensaje de error aquí
        Toast.makeText(fragmentoSesionIndividual.getContext(), "Sesión finalizada con éxito", Toast.LENGTH_SHORT).show();
    }
}
