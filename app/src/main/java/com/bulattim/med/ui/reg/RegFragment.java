package com.bulattim.med.ui.reg;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bulattim.med.R;
import com.bulattim.med.helpers.DBHelper;
import com.bulattim.med.ui.login.LoginFragment;
import com.bulattim.med.ui.main.MainFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

public class RegFragment extends Fragment {
    
    
    private EditText username, email, pass, pass2;
    private FirebaseAuth auth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reg, container, false);
        auth = FirebaseAuth.getInstance();
        username = root.findViewById(R.id.username);
        email = root.findViewById(R.id.email);
        pass = root.findViewById(R.id.password);
        pass2 = root.findViewById(R.id.password2);
        Button bReg = root.findViewById(R.id.bReg);
        Button bToLog = root.findViewById(R.id.bToLog);
        SharedPreferences sp = requireActivity().getSharedPreferences("APP_PREFERNCES", Context.MODE_PRIVATE);
        bReg.setOnClickListener((v -> postDataToSQLite()));
        bToLog.setOnClickListener((v -> {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            LoginFragment fragment = new LoginFragment();
            ft.replace(R.id.host_fragment, fragment);
            ft.commit();
        }));
        return root;
    }
    private void postDataToSQLite() {
        if (username.getText().toString().equals("Guest") || username.getText().toString().equals("Гость")){ Toast.makeText(getContext(), "Неправильное имя", Toast.LENGTH_LONG).show(); return;}
        if (!isEmail(email)) {Toast.makeText(getContext(), "Неправильный email", Toast.LENGTH_LONG).show(); return;}
        if (!pass.getText().toString().equals(pass2.getText().toString()) || pass.getText().length() < 8){ Toast.makeText(getContext(), "Неверный пароль", Toast.LENGTH_LONG).show(); return;}
        auth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString()).addOnCompleteListener(requireActivity(), task -> {
                if (task.isSuccessful()) {
                    String token = task.getResult().getUser().getUid();
                    requireActivity().getSharedPreferences("token", Context.MODE_PRIVATE).edit().putString("token", token).apply();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> doc = DBHelper.getDB();
                    doc.put("username", username.getText().toString());
                    doc.put("email", email.getText().toString());
                    db.collection("users").document(token).set(doc).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            Toast.makeText(getContext(), "Успешно", Toast.LENGTH_LONG).show();
                        }
                    });
                    emptyInputEditText();
                    FragmentManager fm = getParentFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    MainFragment fragment = new MainFragment();
                    ft.replace(R.id.host_fragment, fragment);
                    ft.commit();
                } else {
                    Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void emptyInputEditText() {
        username.setText(null);
        email.setText(null);
        pass.setText(null);
        pass2.setText(null);
    }

    public boolean isEmail(EditText email) {
        String value = email.getText().toString();
        return !value.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches();
    }
}