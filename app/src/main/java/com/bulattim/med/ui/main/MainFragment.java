package com.bulattim.med.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bulattim.med.R;
import com.bulattim.med.helpers.MedViewHolder;
import com.bulattim.med.models.Med;
import com.bulattim.med.ui.add.AddFragment;
import com.bulattim.med.ui.login.LoginFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class MainFragment extends Fragment {

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
        Button bLogOut = root.findViewById(R.id.bExit);
        Button bAdd = root.findViewById(R.id.bAdd);
        TextView username = root.findViewById(R.id.tHello);
        RecyclerView rv = root.findViewById(R.id.rcview);
        final DatabaseReference userdb = FirebaseDatabase.getInstance().getReference().child("users");
        if (auth.getCurrentUser() != null) {
            FirebaseRecyclerOptions<Med> options = new FirebaseRecyclerOptions.Builder<Med>()
                    .setQuery(userdb.child(Objects.requireNonNull(auth.getUid()))
                            .child("Med"), Med.class).build();
            FirebaseRecyclerAdapter<Med, MedViewHolder> Adapter = new FirebaseRecyclerAdapter<Med, MedViewHolder>(options) {
                @NonNull
                @NotNull
                @Override
                public MedViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_med, parent, false);
                    MedViewHolder holder = new MedViewHolder(view);
                    return holder;
                }

                @Override
                protected void onBindViewHolder(@NonNull @NotNull MedViewHolder holder, int position, @NonNull @NotNull Med model) {
                    holder.medView.setText(model.name);
                    holder.timeView.setText(model.time);
                    holder.btn.setOnClickListener(v -> {

                    });
                }
            };
            rv.setAdapter(Adapter);
            Adapter.startListening();
        } else {
            RecyclerView.Adapter<MedViewHolder> Adapter = new RecyclerView.Adapter<MedViewHolder>(){};
            rv.setAdapter(Adapter);
        }

        SharedPreferences sp = requireActivity().getSharedPreferences("APP_PREFERNCES", Context.MODE_PRIVATE);
        if (sp.getString("email", null) == null || auth.getCurrentUser() == null) {
            username.setText("Здравствуйте, Гость!");
            bLogOut.setText("Войти");
            bLogOut.setOnClickListener(v -> getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new LoginFragment()).addToBackStack("").commit());
        } else {
            String name = sp.getString("username", "Гость");
            username.setText(String.format("Здравствуйте, %s!", name));
            bLogOut.setOnClickListener(v -> {
                auth.signOut();
                auth.signInAnonymously();
                sp.edit().remove("username").remove("email").apply();
                getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new LoginFragment()).commit();
            });
        }
        bAdd.setOnClickListener(v -> getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new AddFragment()).addToBackStack("").commit());
        return root;
    }
}