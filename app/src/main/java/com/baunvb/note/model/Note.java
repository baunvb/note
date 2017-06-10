package com.baunvb.note.model;

import java.util.ArrayList;

/**
 * Created by Baunvb on 4/17/2017.
 */

public class Note {
    private int id;
    private String title;
    private String content;
    private String date;
    private String color;
    private int alarm;
    private String time;

    public Note(int id, String title, String content, String date, String time, String color, int alarm) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time;
        this.color = color;
        this.alarm = alarm;
    }

    public Note(String title, String content, String date, String time, String color, int alarm) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time;
        this.color = color;
        this.alarm = alarm;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getColor() {
        return color;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
