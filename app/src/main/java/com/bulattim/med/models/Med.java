package com.bulattim.med.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Med {
    public String name, time;

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
    public String toString() {
        try {
            JSONObject js = new JSONObject();
            js.put("name", name);
            js.put("time", time);
            return js.toString();
        } catch (JSONException e) {
            return "";
        }
    }

    public boolean fromString(String str) {
        try {
            JSONObject js = new JSONObject(str);
            this.name = js.getString("name");
            this.time = js.getString("time");
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    public Med() {
        this.name = "";
        this.time = "";
    }

    public Med(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public Med(Map<String, String> map) {
        this.name = map.get("name");
        this.time = map.get("time");
    }

    public Med(JSONObject json) {
        try {
            this.name = json.getString("name");
            this.time = json.getString("time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("time", time);

        return result;
    }
}
