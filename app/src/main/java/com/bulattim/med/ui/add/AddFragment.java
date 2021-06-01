package com.bulattim.med.ui.add;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class AddFragment extends Fragment {

    public AddFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private EditText name, time;

    JSONArray json;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add, container, false);
        name = root.findViewById(R.id.edName);
        time = root.findViewById(R.id.edTime);
        Button btn = root.findViewById(R.id.bAddMed);
        String token = requireActivity().getSharedPreferences("token", Context.MODE_PRIVATE).getString("token", FirebaseAuth.getInstance().getCurrentUser().getUid());
        btn.setOnClickListener(v -> {
            try {
                FirebaseFirestore.getInstance().collection("users").document(token).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            Map map = doc.getData();
                            try {
                                json = new JSONArray(map.get("med").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                json.put(new JSONObject("{ \"name\":\"" + name.getText().toString() + "\", \"time\":\"" + time.getText().toString() + "\"}"));
                Log.e("Meds", json.toString());
                FirebaseFirestore.getInstance().collection("users").document(token).update("med", json.toString()).addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Успешно", Toast.LENGTH_LONG).show();
                });
                name.setText("");
                time.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return root;
    }
}