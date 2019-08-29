package com.example.cube.Review;

import java.util.Date;

public class ReviewParent {
    private String profile;
    private String review;
    private String user;
    private float rating;
    private String photo;
    private Date date;
    private String comment;
    private Date commentDate;
    private String id;

    public ReviewParent() {
    }


    public ReviewParent(String profile, String review, String user, float rating, String photo, Date date, String comment, Date commentDate, String id) {
        this.profile = profile;
        this.review = review;
        this.user = user;
        this.rating = rating;
        this.photo = photo;
        this.date = date;
        this.comment = comment;
        this.commentDate = commentDate;
        this.id = id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public void setId(String Id) { this.id = Id;}

    public String getId() { return id; }

    public void setProfile(String profile) { this.profile = profile; }

    public String getProfile( ) { return profile; }

    @Override
    public String toString() {
        return "ReviewParent{" +
                "review='" + review + '\'' +
                ", user='" + user + '\'' +
                ", rating=" + rating +
                ", photo='" + photo + '\'' +
                ", date=" + date +
                ", comment='" + comment + '\'' +
                ", commentDate=" + commentDate +
                '}';
    }
}
