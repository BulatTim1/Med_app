package com.bulattim.med.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bulattim.med.R;
import com.bulattim.med.ui.add.AddFragment;
import com.bulattim.med.ui.login.LoginFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainFragment extends Fragment {

    private RecyclerView rv;
    private FirebaseAuth auth;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        Button bLogOut = root.findViewById(R.id.bExit);
        Button bAdd = root.findViewById(R.id.bAdd);
        TextView username = root.findViewById(R.id.tHello);
        rv = root.findViewById(R.id.rcview);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(manager);
        SharedPreferences sp = requireActivity().getPreferences(Context.MODE_PRIVATE);
        String email = sp.getString("email", null);
        if (email == null && user == null) {
            username.setText("Здравствуйте, Гость!");
            bLogOut.setText("Войти");
            bLogOut.setOnClickListener(v -> {
                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                LoginFragment fragment = new LoginFragment();
                ft.replace(R.id.host_fragment, fragment);
                ft.commit();
            });
        } else {
            String name = sp.getString("username", "Гость");
            username.setText(String.format("Здравствуйте, %s!", name));
            bLogOut.setOnClickListener(v -> {
                auth.signInAnonymously();
                SharedPreferences.Editor edt = sp.edit();
                edt.remove("username");
                edt.remove("email");
                edt.apply();
                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                LoginFragment fragment = new LoginFragment();
                ft.replace(R.id.host_fragment, fragment);
                ft.commit();
            });
        }
        bAdd.setOnClickListener(v -> {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            AddFragment fragment = new AddFragment();
            ft.replace(R.id.host_fragment, fragment);
            ft.commit();
        });
        return root;
    }
}