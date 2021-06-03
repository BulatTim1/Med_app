package com.bulattim.med.ui.main;

import android.content.Context;
import android.content.Intent;
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

import com.bulattim.med.MedNotificator;
import com.bulattim.med.R;
import com.bulattim.med.helpers.DBHelper;
import com.bulattim.med.helpers.MedAdapter;
import com.bulattim.med.models.Med;
import com.bulattim.med.models.User;
import com.bulattim.med.ui.add.AddFragment;
import com.bulattim.med.ui.login.LoginFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;


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
        if (!MedNotificator.getState())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getContext().startForegroundService(new Intent(getActivity(), MedNotificator.class).setAction("ACTION_START_FOREGROUND_SERVICE"));
            }
        if (token.equals("")) {
            auth.signOut();
            auth.signInAnonymously().addOnSuccessListener(task -> {
                token = task.getUser().getUid();
                requireActivity().getSharedPreferences("token", Context.MODE_PRIVATE).edit().putString("token", token).apply();
            });
            DBHelper.updateDB(getContext());
        }
        try {
            if (auth.getCurrentUser().isAnonymous()) {
                username.setText("Здравствуйте, Гость!");
                bLogOut.setText("Войти");
                bLogOut.setOnClickListener(v -> {
                    getContext().stopService(new Intent(getActivity(), MedNotificator.class).setAction("ACTION_STOP_FOREGROUND_SERVICE"));
                    getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new LoginFragment()).addToBackStack("").commit();
                });
            } else {
                DocumentSnapshot doc = DBHelper.getDB(getContext());
                if (doc != null) if (doc.exists()) {
                    ArrayList<Med> mList = new ArrayList<>();
                    Map<String, Object> map = doc.getData();
                    Log.e("DB", map.values().toString());
                    try {
                        JSONArray json = new JSONArray(map.get("med").toString());
                        for (int i = 0; i < json.length(); i++) {
                            Med med = new Med(json.getJSONObject(i));
                            mList.add(med);
                            Log.e("DB", json.getString(i));
                        }
                        Log.e("DB", map.get("med").toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ListView listView = root.findViewById(R.id.lview);
                    MedAdapter adapter = new MedAdapter(getContext(), mList);
                    listView.setAdapter(adapter);
                    username.setText(("Здравствуйте, " + map.get("username") + "!"));
                } else {
                    username.setText("Здраствуйте, Гость!");
                }
                bLogOut.setOnClickListener(v -> {
                    auth.signOut();
                    auth.signInAnonymously().addOnCompleteListener(task -> {
                    });
                    getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new LoginFragment()).commit();
                });
            }
        } catch (NullPointerException e){
            auth.signOut();
            auth.signInAnonymously().addOnSuccessListener(task -> {
                token = task.getUser().getUid();
                requireActivity().getSharedPreferences("token", Context.MODE_PRIVATE).edit().putString("token", token).apply();
            });
        }
        bAdd.setOnClickListener(v -> {
            getContext().stopService(new Intent(getActivity(), MedNotificator.class).setAction("ACTION_STOP_FOREGROUND_SERVICE"));
            getParentFragmentManager().beginTransaction().replace(R.id.host_fragment, new AddFragment()).addToBackStack("").commit();
        });
        return root;
    }
}