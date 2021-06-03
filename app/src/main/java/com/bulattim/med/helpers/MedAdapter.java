package com.bulattim.med.helpers;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.bulattim.med.MainActivity;
import com.bulattim.med.R;
import com.bulattim.med.models.Med;
import com.bulattim.med.ui.change.ChangeFragment;
import com.bulattim.med.ui.login.LoginFragment;

import java.util.List;



public class MedAdapter extends ArrayAdapter<Med> {
    Context context;
    public MedAdapter(Context context, List<Med> object){
        super(context,0, object);
        this.context = context;
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
//            ((Activity) context).getFragmentManager().beginTransaction().replace(R.layout.fragment_change, new ChangeFragment()).addToBackStack("").commit();
        });


        return view;
    }

}