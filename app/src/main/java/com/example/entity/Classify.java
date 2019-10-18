package com.example.entity;

public class Classify {
    private int id;
    private String classname;

    public Classify(int id, String classname) {
        this.id = id;
        this.classname = classname;
    }

    public Classify() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }
}
