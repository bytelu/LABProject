package com.example.labproject.entidades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.labproject.R;

import java.util.ArrayList;

public class ProfesorAdapter extends RecyclerView.Adapter<ProfesorAdapter.ViewHolder> {
    private ArrayList<Profesor> mData;
    private LayoutInflater mInfater;
    private Context context;

    public ProfesorAdapter(ArrayList<Profesor> itemList, Context context){
        this.mInfater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
    }


    @Override
    public int getItemCount(){return mData.size(); }

    @Override
    public ProfesorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInfater.inflate(R.layout.list_element_profesores, parent, false);
        return new ProfesorAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ProfesorAdapter.ViewHolder holder, final int position){
        holder.bindData(mData.get(position));
    }

    public void setItems(ArrayList<Profesor> items) { mData = items; }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iconImage;
        TextView name, PLastName, MLastName, Boleta;

        ViewHolder(View itemView){
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconElementView);
            name = itemView.findViewById(R.id.NameTextView);
            PLastName = itemView.findViewById(R.id.PLastNameTextView);
            MLastName = itemView.findViewById(R.id.MLastNameTextView);
            Boleta = itemView.findViewById(R.id.BoletaTextView);
        }


        void bindData(final Profesor item){
            name.setText(item.getNOMBRE());
            PLastName.setText(item.getAPELLIDO_P());
            MLastName.setText(item.getAPELLIDO_M());
            Boleta.setText(item.getBOLETA());
        }

    }
}
