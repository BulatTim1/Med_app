package com.bulattim.med.helpers;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bulattim.med.R;


public class MedViewHolder extends RecyclerView.ViewHolder{
    public final TextView medView;
    public final TextView timeView;
    public final Button btn;

    public MedViewHolder(View itemView) {
        super(itemView);

        medView = itemView.findViewById(R.id.tMed);
        timeView = itemView.findViewById(R.id.tTime);
        btn = itemView.findViewById(R.id.bChange);
    }
}
