package com.bulattim.med.ui.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bulattim.med.R;
import com.bulattim.med.models.User;
import com.bulattim.med.ui.main.MainFragment;
import com.bulattim.med.ui.reg.RegFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;

public class LoginFragment extends Fragment {
    private EditText email, pass;
    private FirebaseAuth auth;
    private SharedPreferences.Editor edt;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        email = root.findViewById(R.id.email);
        pass = root.findViewById(R.id.password);
        Button bLog = root.findViewById(R.id.bLog);
        Button bToReg = root.findViewById(R.id.bRegFLog);
        Button bAnom = root.findViewById(R.id.bGuest);
        auth = FirebaseAuth.getInstance();
        SharedPreferences sp = requireActivity().getSharedPreferences("APP_PREFERNCESS", Context.MODE_PRIVATE);
        bLog.setOnClickListener((v -> {
            if (!isEmail(email)) {Toast.makeText(getContext(), "Неправильная почта!", Toast.LENGTH_LONG).show(); return;}
            auth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString()).addOnCompleteListener(requireActivity(), task -> {
                if (task.isSuccessful()) {
                    String token = task.getResult().getUser().getUid();
                    requireActivity().getSharedPreferences("token", Context.MODE_PRIVATE).edit().putString("token", token).apply();
                    User user = new User();
                    try {
                        FirebaseFirestore.getInstance().collection("users").document(token).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot document = task1.getResult();
                                if (document.exists()) {
                                    user.setName(String.valueOf(document.get("username")));
                                    user.setEmail(String.valueOf(document.get("email")));
                                    try {
                                        user.setMed(new JSONArray(document.get("med").toString()));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(getContext(), "Успешно", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.d("Firestore", "No such document");
                                }
                            } else {
                                Log.d("Firestore", "get failed with ", task1.getException());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    emptyInputEditText();
                    getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new MainFragment()).commit();
                } else {
                    Toast.makeText(getContext(), "Не удалось войти", Toast.LENGTH_LONG).show();
                }
            });
        }));
        bToReg.setOnClickListener((v -> getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new RegFragment()).addToBackStack("").commit()));
        bAnom.setOnClickListener(v -> {
            auth.signInAnonymously().addOnSuccessListener(task -> {
                String token = task.getUser().getUid();
                requireActivity().getSharedPreferences("token", Context.MODE_PRIVATE).edit().putString("token", token).apply();
            });
            getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new MainFragment()).commit();
        });
        return root;
    }

    private void emptyInputEditText() {
        email.setText(null);
        pass.setText(null);
    }

    public boolean isEmail(EditText email) {
        String value = email.getText().toString();
        return !value.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches();
    }
}