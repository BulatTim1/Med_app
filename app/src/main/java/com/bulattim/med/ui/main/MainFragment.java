package com.bulattim.med.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bulattim.med.R;
import com.bulattim.med.helpers.MedAdapter;
import com.bulattim.med.models.Med;
import com.bulattim.med.models.User;
import com.bulattim.med.ui.add.AddFragment;
import com.bulattim.med.ui.login.LoginFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Map;


public class MainFragment extends Fragment {

    private FirebaseAuth auth;


    public MainFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    String token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        auth = FirebaseAuth.getInstance();
        Button bLogOut = root.findViewById(R.id.bExit);
        Button bAdd = root.findViewById(R.id.bAdd);
        TextView username = root.findViewById(R.id.tHello);
        User user = new User();
        token = requireActivity().getSharedPreferences("token", Context.MODE_PRIVATE).getString("token", "");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (token.equals("")) {
            auth.signOut();
            auth.signInAnonymously().addOnSuccessListener(task -> {
                token = task.getUser().getUid();
                requireActivity().getSharedPreferences("token", Context.MODE_PRIVATE).edit().putString("token", token).apply();
            });
            username.setText("Здравствуйте, Гость!");
            bLogOut.setText("Войти");
            bLogOut.setOnClickListener(v -> getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new LoginFragment()).addToBackStack("").commit());
        } else if (auth.getCurrentUser().isAnonymous()) {
            username.setText("Здравствуйте, Гость!");
            bLogOut.setText("Войти");
            bLogOut.setOnClickListener(v -> getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new LoginFragment()).addToBackStack("").commit());
        } else {
            db.collection("users").document(token).get().addOnCompleteListener(task -> {
                ArrayList<Med> mList = new ArrayList<>();
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Map map = doc.getData();
                        Log.e("DB", map.values().toString());
                        user.setName(map.get("username").toString());
                        user.setEmail(map.get("email").toString());
                        try {
                            JSONArray json = new JSONArray(map.get("med").toString());
                            for (int i = 0; i < json.length(); i++) {
                                Med med = new Med(json.getJSONObject(i));
                                mList.add(med);
                                Log.e("DB", json.getString(i));
                            }
                            user.setMed(map.get("med").toString());
                            Log.e("DB", map.get("med").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ListView listView = root.findViewById(R.id.lview);
                        MedAdapter adapter = new MedAdapter(getContext(), mList);
                        listView.setAdapter(adapter);
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            });
            username.setText(("Здравствуйте, " + user.getName() + "!"));
            bLogOut.setOnClickListener(v -> {
                auth.signOut();
                auth.signInAnonymously().addOnCompleteListener(task -> {
                });
                getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new LoginFragment()).commit();
            });
        }
        bAdd.setOnClickListener(v -> getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new AddFragment()).addToBackStack("").commit());
        return root;
    }
}