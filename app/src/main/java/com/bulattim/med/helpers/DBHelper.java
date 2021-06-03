package com.bulattim.med.helpers;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DBHelper {
    static DocumentSnapshot doc;
    static FirebaseAuth auth;

    public static void updateDB(Context context){
        auth = FirebaseAuth.getInstance();
        String token = context.getSharedPreferences("token", Context.MODE_PRIVATE).getString("token", "");
        if (!token.equals("")) {
            FirebaseFirestore.getInstance().collection("users").document(token).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) doc = task.getResult();
            });
        }
    }

    public static DocumentSnapshot getDB(){
        return doc;
    }

    public static DocumentSnapshot getDB(Context context){
        updateDB(context);
        return doc;
    }
}
