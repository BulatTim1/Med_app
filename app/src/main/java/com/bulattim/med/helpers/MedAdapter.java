package com.bulattim.med.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bulattim.med.R;
import com.bulattim.med.models.Med;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MedAdapter extends RecyclerView.Adapter<MedAdapter.MyAdapter> {

    ArrayList<Med> meds;

    public MedAdapter(String meds_json){
        try {
            JSONArray medds = new JSONArray(meds_json);
            for (int i = 0; i < medds.length(); i++){
                Med m = new Med();
                m.setName(((JSONObject) medds.get(i)).getString("name"));
                m.setTime(((JSONObject) medds.get(i)).getString("time"));
                meds.add(m);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class MyAdapter extends RecyclerView.ViewHolder{

        public TextView name;
        public TextView time;
        public Button btn;

        public MyAdapter(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tMed);
            time = itemView.findViewById(R.id.tTime);
            btn = itemView.findViewById(R.id.bChange);
        }
    }

    @NonNull
    @Override
    public MyAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_med,parent, false);
        return new MyAdapter(v);
    }


    @Override
    public void onBindViewHolder(@NonNull MyAdapter holder, int position) {
        holder.name.setText(meds.get(position).getName());
        holder.time.setText(String.valueOf(meds.get(position).getTime()));
    }

    @Override
    public int getItemCount() {
        return meds.size();
    }
}