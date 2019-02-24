package com.aaronsng.wheresitgo.model;

import java.io.Serializable;

/**
 * Created by aaron on 04-Mar-17.
 */

public class Category implements Serializable{
    String category_id;
    String category_title;
    String category_description;
    String user_id;

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_title() {
        return category_title;
    }

    public void setCategory_title(String category_title) {
        this.category_title = category_title;
    }

    public String getCategory_description() {
        return category_description;
    }

    public void setCategory_description(String category_description) {
        this.category_description = category_description;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Category(String category_id, String category_title, String category_description, String user_id) {
        this.category_id = category_id;
        this.category_title = category_title;
        this.category_description = category_description;
        this.user_id = user_id;
    }
}
