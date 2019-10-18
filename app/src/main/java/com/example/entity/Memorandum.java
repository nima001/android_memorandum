package com.example.entity;

import java.sql.Date;

public class Memorandum {
    private int id;//ID
    private String title;//主题
    private String date;//时间
    private String classify;//分类
    private String remarks;//备注
    private int status;//状态

    public Memorandum(int id, String title, String date, String classify, String remarks, int status) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.classify = classify;
        this.remarks = remarks;
        this.status = status;
    }

    public Memorandum() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
