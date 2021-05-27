package com.bulattim.med.helpers;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bulattim.med.R;


public class MedViewHolder extends RecyclerView.ViewHolder{
    public TextView medView;
    public TextView timeView;
    public Button btn;

    public MedViewHolder(View itemView) {
        super(itemView);

        medView = (TextView) itemView.findViewById(R.id.tMed);
        timeView = (TextView) itemView.findViewById(R.id.tTime);
        btn = itemView.findViewById(R.id.bChange);
    }
}
