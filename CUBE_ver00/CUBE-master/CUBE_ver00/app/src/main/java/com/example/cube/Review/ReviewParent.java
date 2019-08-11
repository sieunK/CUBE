package com.example.cube.Review;

public class ReviewParent {
    private String review;
    private String userID;
    private float rating;
    private String date;
    private String comment;

    public ReviewParent() {
    }

    public ReviewParent(String userID, String date, float rating, String review) {
        this.userID = userID;
        this.date = date;
        this.rating = rating;
        this.review = review;
    }

    public ReviewParent(String userID, String date, float rating, String review, String comment) {
        this.userID = userID;
        this.date = date;
        this.rating = rating;
        this.review = review;
        this.comment = comment;
    }


    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
