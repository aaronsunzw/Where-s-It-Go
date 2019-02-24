package com.aaronsng.wheresitgo.model;

import java.io.Serializable;

public class User implements Serializable{
    String user_id;
    String name;
    String username;
    String email;
    String password;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User(String user_id, String name, String username, String email, String password) {
        this.user_id = user_id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
