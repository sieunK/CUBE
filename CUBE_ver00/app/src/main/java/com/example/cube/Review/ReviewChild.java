package com.example.cube.Review;

public class ReviewChild {
    private String time;
    private String comment;

    public ReviewChild() {
    }

    public ReviewChild(String time, String comment) {
        this.time = time;
        this.comment = comment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
