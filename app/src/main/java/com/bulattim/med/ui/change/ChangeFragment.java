package com.bulattim.med.ui.change;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bulattim.med.R;
import com.bulattim.med.helpers.DBHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangeFragment extends Fragment {

    JSONArray json;
    private EditText name, time;

    public ChangeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add, container, false);
        name = root.findViewById(R.id.edName);
        time = root.findViewById(R.id.edTime);
        Button btn = root.findViewById(R.id.bAddMed);
        String token = requireActivity().getSharedPreferences("token", Context.MODE_PRIVATE).getString("token", "");
        btn.setOnClickListener(v -> {
            if (time.getText().length() > 5)
                Toast.makeText(getContext(), "Неверный формат времени", Toast.LENGTH_SHORT).show();
            else {
                String[] hm = time.getText().toString().split(":");
                String hh = hm[0];
                String mm = hm[1];
                String n = name.getText().toString();
                String t = time.getText().toString();
                if (Integer.parseInt(hh) >= 24 || Integer.parseInt(mm) >= 60)
                    Toast.makeText(getContext(), "Неверный формат времени", Toast.LENGTH_SHORT).show();
                else {
                    if (!token.equals("")) {
                        try {
                            Map<String, Object> doc = DBHelper.getDB(getContext());
                            try {
                                json = new JSONArray(doc.get("med").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                json = new JSONArray();
                            }
                            Map<String, Object> map = new HashMap<>();
                            map.put("name", n);
                            map.put("time", t);
                            json.put(new JSONObject(map));
                            Log.e("Meds", json.toString());
                            FirebaseFirestore.getInstance().collection("users").document(token).update("med", json.toString()).addOnSuccessListener(documentReference -> Toast.makeText(getContext(), "Успешно", Toast.LENGTH_LONG).show());
                            name.setText("");
                            time.setText("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        return root;
    }
}