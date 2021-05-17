package com.bulattim.med.models;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class Med {
    private String name, time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @NotNull
    public String toString(){
        try {
            JSONObject js = new JSONObject();
            js.put("name", name);
            js.put("time", time);
            return js.toString();
        } catch (JSONException e) {
            return "";
        }
    }

    public boolean fromString(String str){
        try {
            JSONObject js = new JSONObject(str);
            this.name = js.getString("name");
            this.time = js.getString("time");
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
}
