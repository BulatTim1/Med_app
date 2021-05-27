package com.bulattim.med.ui.add;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.bulattim.med.R;
import com.bulattim.med.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddFragment extends Fragment {

    public AddFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private EditText name, time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add, container, false);
        name = root.findViewById(R.id.edName);
        time = root.findViewById(R.id.edTime);
        Button btn = root.findViewById(R.id.bAddMed);
        btn.setOnClickListener(v -> {
            SharedPreferences sp = requireActivity().getSharedPreferences("APP_PREFERNCES", Context.MODE_PRIVATE);
            SharedPreferences.Editor edt = sp.edit();
            String meds = sp.getString("med", "[]");
            try {
                JSONArray med = meds.equals("{}") ? new JSONArray() : new JSONArray(meds);
                med.put(new JSONObject().put("name", name.getText().toString()).put("time", time.getText().toString()));
                edt.putString("med", med.toString());
                edt.apply();
                User user = new User();
                user.setEmail(sp.getString("email", ""));
                user.setMed(med.toString());
                user.setName(sp.getString("username", ""));
                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("med").setValue(med.toString());
                name.setText("");
                time.setText("");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        return root;
    }
}