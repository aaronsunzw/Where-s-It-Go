package com.aaronsng.wheresitgo.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Member;

import com.aaronsng.wheresitgo.model.User;

public class StoredObject {

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private Context context;

    public StoredObject(Context context) {
        this.context = context;

    }

    public void clearStoredObject() {
        SharedPreferences settings = context.getSharedPreferences(
                "wheresitgoDetails", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();


    }

    public void storeObject(User obj) {
        SharedPreferences settings = context.getSharedPreferences("wheresitgoDetails", 0);
        SharedPreferences.Editor editor = settings.edit();

        Gson gson = new Gson();
        String json = gson.toJson(obj);
        editor.putString("userDetailsObj", json);
        editor.commit();
        try {

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public User getUserDetails() {
        User userDetailsObj = null;
        try {

            SharedPreferences settings = context.getSharedPreferences(
                    "wheresitgoDetails", 0);
            Gson gson = new Gson();
            String json = settings.getString("userDetailsObj", "");
            userDetailsObj = gson.fromJson(json, User.class);
        } catch (Exception e) {
            return null;
        }

        return userDetailsObj;

    }

    public User clearUserDetails() {
        User userDetailsObj = null;
        try {

            SharedPreferences settings = context.getSharedPreferences(
                    "wheresitgoDetails", 0);
            Gson gson = new Gson();
            String json = settings.getString("userDetailsObj", "");
            userDetailsObj = null;
        } catch (Exception e) {
            return null;
        }

        return userDetailsObj;

    }

    //URL Start
    public void storeServerUrl(String url) {
        SharedPreferences settings = context.getSharedPreferences("wheresitgoDetails", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("serverUrl", url);
        editor.commit();
        try {

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public String getServerUrl() {
        String serverUrl = null;
        try {

            SharedPreferences settings = context.getSharedPreferences(
                    "wheresitgoDetails", 0);
            serverUrl = settings.getString("serverUrl", "");
        } catch (Exception e) {
            return null;
        }

        return serverUrl;

    }



    public void clearServerUrl( ) {
        SharedPreferences settings = context.getSharedPreferences("wheresitgoDetails", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("serverUrl", null);
        editor.commit();
        try {

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    //URL End

}
