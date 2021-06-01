package com.bulattim.med.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class User {
    private String name;
    private String email;
    private ArrayList<Med> med;

    public User(){
        name = "";
        email = "";
        med = new ArrayList<>();
    }

    public User(String name, String email){
        this.name = name;
        this.email = email;
        med = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMedString() {
        JSONObject js = new JSONObject();
        try {
            js.put("med", med);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return js.toString();
    }

    public void setMed(ArrayList<Med> med){
        Iterator<Med> iter = med.iterator();
        this.med = new ArrayList<>();
        while(iter.hasNext()){
            this.med.add(iter.next());
        }
    }

    public void setMed(String json){
        try {
            JSONArray arr = new JSONArray(new JSONObject(json));
            this.med = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++){
                this.med.add(new Med(arr.getJSONObject(i).getString("name"), arr.getJSONObject(i).getString("time")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setMed(JSONObject json){
        try {
            JSONArray arr = new JSONArray(json);
            this.med = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++){
                this.med.add(new Med(arr.getJSONObject(i).getString("name"), arr.getJSONObject(i).getString("time")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setMed(JSONArray json){
        try {
            this.med = new ArrayList<>();
            for (int i = 0; i < json.length(); i++){
                this.med.add(new Med(json.getJSONObject(i).getString("name"), json.getJSONObject(i).getString("time")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Med> getArrayMed() {
        return med;
    }

    public JSONArray getJSONMed() {
        return new JSONArray(med);
    }
}
