package com.aaronsng.wheresitgo.model;

import java.io.Serializable;

/**
 * Created by aaron on 05-Mar-17.
 */

public class Record implements Serializable{
    String record_id;
    String record_title;
    String record_description;
    String record_image;
    String record_audio;
    String category_id;

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

    public String getRecord_title() {
        return record_title;
    }

    public void setRecord_title(String record_title) {
        this.record_title = record_title;
    }

    public String getRecord_description() {
        return record_description;
    }

    public void setRecord_description(String record_description) {
        this.record_description = record_description;
    }

    public String getRecord_image() {
        return record_image;
    }

    public void setRecord_image(String record_image) {
        this.record_image = record_image;
    }

    public String getRecord_audio() {
        return record_audio;
    }

    public void setRecord_audio(String record_audio) {
        this.record_audio = record_audio;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public Record(String record_id, String record_title, String record_description, String record_image, String record_audio, String category_id) {
        this.record_id = record_id;
        this.record_title = record_title;
        this.record_description = record_description;
        this.record_image = record_image;
        this.record_audio = record_audio;
        this.category_id = category_id;
    }
}
