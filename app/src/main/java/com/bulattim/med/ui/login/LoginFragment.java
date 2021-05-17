package com.bulattim.med.ui.login;

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
import com.bulattim.med.ui.main.MainFragment;
import com.bulattim.med.ui.reg.RegFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginFragment extends Fragment {
    private EditText email, pass;
    private FirebaseAuth auth;
    private SharedPreferences.Editor edt;
    private DBHelper db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        email = root.findViewById(R.id.email);
        pass = root.findViewById(R.id.password);
        Button bLog = root.findViewById(R.id.bLog);
        Button bToReg = root.findViewById(R.id.bRegFLog);
        Button bAnom = root.findViewById(R.id.bGuest);
        auth = FirebaseAuth.getInstance();
        db = new DBHelper(getContext());
        SharedPreferences sp = requireActivity().getPreferences(Context.MODE_PRIVATE);
        edt = sp.edit();
        bLog.setOnClickListener((v -> verifyFromSQLite()));
        bToReg.setOnClickListener((v -> {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            RegFragment fragment = new RegFragment();
            ft.replace(R.id.host_fragment, fragment);
            ft.commit();
        }));
        bAnom.setOnClickListener(v -> auth.signInAnonymously());
        edt.apply();
        return root;
    }

    private void verifyFromSQLite() {
        if (!isEmail(email)) {Toast.makeText(getContext(), "Неправильная почта!", Toast.LENGTH_LONG).show(); return;}
        auth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString()).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                edt.putString("username", email.getText().toString());
                edt.putString("email", Objects.requireNonNull(auth.getCurrentUser()).getEmail());
                edt.putString("med", db.getMed(email.getText().toString()));
                edt.apply();
                emptyInputEditText();
                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                MainFragment fragment = new MainFragment();
                ft.replace(R.id.host_fragment, fragment);
                ft.commit();
            } else {
                Toast.makeText(getContext(), "Не удалось войти", Toast.LENGTH_LONG).show();
            }
        });
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