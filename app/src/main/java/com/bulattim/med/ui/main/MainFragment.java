package com.bulattim.med.ui.main;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.work.WorkManager;

import com.bulattim.med.MedNotificator;
import com.bulattim.med.R;
import com.bulattim.med.helpers.DBHelper;
import com.bulattim.med.helpers.MedAdapter;
import com.bulattim.med.models.Med;
import com.bulattim.med.ui.add.AddFragment;
import com.bulattim.med.ui.login.LoginFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainFragment extends Fragment {

    String token;
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
        token = requireActivity().getSharedPreferences("token", Context.MODE_PRIVATE).getString("token", "");
        DBHelper.updateDB(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WorkManager.getInstance().enqueue(MedNotificator.getWork());
        }
        if (token.equals("")) {
            auth.signOut();
            auth.signInAnonymously().addOnSuccessListener(task -> {
                token = task.getUser().getUid();
                requireActivity().getSharedPreferences("token", Context.MODE_PRIVATE).edit().putString("token", token).apply();
            });
            Map<String, Object> map = new HashMap<>();
            map.put("username", "Гость");
            map.put("email", "");
            map.put("med", "[]");
            FirebaseFirestore.getInstance().collection("users").document(token).set(map).addOnSuccessListener(task -> Log.e("Guest", "Successful"));
            DBHelper.updateDB(getContext());
        }
        try {
            Map<String, Object> map = DBHelper.getDB(getContext());
            if (auth.getCurrentUser().isAnonymous()) {
                username.setText("Здравствуйте, Гость!");
                bLogOut.setText("Войти");
                bLogOut.setOnClickListener(v -> {
                    MedNotificator.stopWork();
                    getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new LoginFragment()).addToBackStack("").commit();
                });
            } else {
                username.setText(("Здравствуйте, " + map.get("username") + "!"));
                bLogOut.setText("Выйти");
                bLogOut.setOnClickListener(v -> {
                    getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new LoginFragment()).addToBackStack("").commit();
                });
            }
            try {
                if (map.size() == 3) {
                    ArrayList<Med> mList = new ArrayList<>();
                    try {
                        JSONArray json = new JSONArray(map.get("med").toString());
                        for (int i = 0; i < json.length(); i++) {
                            Med med = new Med(json.getJSONObject(i));
                            mList.add(med);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ListView listView = root.findViewById(R.id.lview);
                    MedAdapter adapter = new MedAdapter(getContext(), mList);
                    listView.setAdapter(adapter);
                }
            } catch (NullPointerException e){
                Map<String, Object> map1 = new HashMap<>();
                map1.put("username", "Гость");
                map1.put("email", "");
                map1.put("med", "[]");
                FirebaseFirestore.getInstance().collection("users").document(token).set(map1).addOnSuccessListener(task -> Log.e("Guest", "Successful"));
                DBHelper.updateDB(getContext());
                ArrayList<Med> mList = new ArrayList();
                ListView listView = root.findViewById(R.id.lview);
                MedAdapter adapter = new MedAdapter(getContext(), mList);
                listView.setAdapter(adapter);
            }
            bLogOut.setOnClickListener(v -> {
                auth.signOut();
                auth.signInAnonymously().addOnSuccessListener(task -> {
                    token = task.getUser().getUid();
                    requireActivity().getSharedPreferences("token", Context.MODE_PRIVATE).edit().putString("token", token).apply();
                });
                MedNotificator.stopWork();
                getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new LoginFragment()).addToBackStack("").commit();
            });

        } catch (NullPointerException e) {
            String tokenLast = token;
            auth.signInAnonymously().addOnSuccessListener(task -> {
                token = task.getUser().getUid();
            });
            if (!token.equals(tokenLast)) {
                requireActivity().getSharedPreferences("token", Context.MODE_PRIVATE).edit().putString("token", token).apply();
                Map<String, Object> map = new HashMap<>();
                map.put("username", "Гость");
                map.put("email", "");
                map.put("med", "[]");
                FirebaseFirestore.getInstance().collection("users").document(token).set(map).addOnSuccessListener(task -> Log.e("Guest", "Successful"));
            }
            Map<String, Object> map = DBHelper.getDB(getContext());
            ArrayList<Med> mList = new ArrayList<>();
            try {
                JSONArray json = new JSONArray(map.get("med").toString());
                for (int i = 0; i < json.length(); i++) {
                    Med med = new Med(json.getJSONObject(i));
                    mList.add(med);
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            ListView listView = root.findViewById(R.id.lview);
            MedAdapter adapter = new MedAdapter(getContext(), mList);
            listView.setAdapter(adapter);
            username.setText("Здравствуйте, Гость!");
            bLogOut.setText("Войти");
            bLogOut.setOnClickListener(v -> {
                MedNotificator.stopWork();
                getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new LoginFragment()).addToBackStack("").commit();
            });
        }
        bAdd.setOnClickListener(v -> getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new AddFragment()).addToBackStack("").commit());
        return root;
    }
}