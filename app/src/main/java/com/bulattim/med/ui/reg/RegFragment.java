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
import com.bulattim.med.models.User;
import com.bulattim.med.ui.login.LoginFragment;
import com.bulattim.med.ui.main.MainFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RegFragment extends Fragment {
    
    
    private EditText username, email, pass, pass2;
    private DBHelper db;
    private User user;
    private SharedPreferences.Editor edt;
    private FirebaseAuth auth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reg, container, false);
        auth = FirebaseAuth.getInstance();
        username = (EditText) root.findViewById(R.id.username);
        email = (EditText) root.findViewById(R.id.email);
        pass = (EditText) root.findViewById(R.id.password);
        pass2 = (EditText) root.findViewById(R.id.password2);
        Button bReg = root.findViewById(R.id.bReg);
        Button bToLog = root.findViewById(R.id.bToLog);
        SharedPreferences sp = requireActivity().getPreferences(Context.MODE_PRIVATE);
        edt = sp.edit();
        bReg.setOnClickListener((v -> postDataToSQLite()));
        bToLog.setOnClickListener((v -> {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            LoginFragment fragment = new LoginFragment();
            ft.replace(R.id.host_fragment, fragment);
            ft.commit();
        }));
        db = new DBHelper(this.getActivity());
        user = new User();
        edt.apply();
        return root;
    }
    private void postDataToSQLite() {
        if (username.getText().toString().equals("Guest") || username.getText().toString().equals("Гость")){ Toast.makeText(getContext(), "Неправильное имя", Toast.LENGTH_LONG).show(); return;}
        if (!isEmail(email)) {Toast.makeText(getContext(), "Неправильный email", Toast.LENGTH_LONG).show(); return;}
        if (!pass.getText().toString().equals(pass2.getText().toString()) || pass.getText().length() < 8){ Toast.makeText(getContext(), "Неверный пароль", Toast.LENGTH_LONG).show(); return;}
        if (!db.checkUser(email.getText().toString())) {
            auth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString()).addOnCompleteListener(requireActivity(), task -> {
                if (task.isSuccessful()) {
                    user.setName(username.getText().toString());
                    user.setEmail(email.getText().toString());
                    db.addUser(user);
                    edt.putString("username", username.getText().toString());
                    edt.putString("email", email.getText().toString());
                    edt.apply();
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
        } else {
            Toast.makeText(getContext(), "Пользователь существует", Toast.LENGTH_LONG).show();
        }
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