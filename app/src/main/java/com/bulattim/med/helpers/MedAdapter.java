package com.bulattim.med.helpers;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bulattim.med.R;
import com.bulattim.med.models.Med;

import java.util.List;



public class MedAdapter extends ArrayAdapter<Med> {
    public MedAdapter(Context context, List<Med> object){
        super(context,0, object);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        if(view == null){
            view =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.card_med,parent,false);
        }

        TextView name = view.findViewById(R.id.tMed);
        TextView time = view.findViewById(R.id.tTime);
        Button change = view.findViewById(R.id.bChange);

        Med med = getItem(position);

        name.setText(med.getName());
        time.setText(med.getTime());
        change.setOnClickListener(v -> {
            //TODO: change time or name of med
        });


        return view;
    }

}