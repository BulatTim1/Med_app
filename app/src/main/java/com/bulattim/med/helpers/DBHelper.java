package com.bulattim.med.helpers;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DBHelper {
    static Map<String, Object> doc;
    static FirebaseAuth auth;

    public static void updateDB(Context context){
        auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String token = context.getSharedPreferences("token", Context.MODE_PRIVATE).getString("token", "");
        if (!token.equals("")) {
            Log.e("DBerror", token + " " + token.toString());
            db.collection("users").document(token).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) doc = task.getResult().getData();
                else doc = new HashMap<>();
            });
        } else {
            doc = new HashMap<>();
        }
    }

    public static Map<String, Object> getDB(){
        return doc;
    }

    public static Map<String, Object> getDB(Context context){
        updateDB(context);
        return doc;
    }
}
