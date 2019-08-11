package com.example.cube.Components;

import java.util.Date;

public class NoticeData {
    private String id;
    private String title;
    private String content;
    private Date date;
    private int numComments;
    private int numClicks;
    private int filenum;
    public NoticeData(){}

    public NoticeData(String id, String title, String content, Date date,  int numClicks,int numComments, int filenum) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.numClicks = numClicks;
        this.numComments = numComments;
        this.filenum = filenum;
    }

    public int getFilenum() {
        return filenum;
    }

    public void setFilenum(int filenum) {
        this.filenum = filenum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public int getNumClicks() {
        return numClicks;
    }

    public void setNumClicks(int numClicks) {
        this.numClicks = numClicks;
    }

    @Override
    public String toString() {
        return "NoticeData{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", numClicks=" + numClicks +
                ", numComments=" + numComments +
                ", filenum=" + filenum +
                '}';
    }
}