package com.pi.ising.model;

public class Post {
    private String Postid;
    private String postimage;
    private String description;
private  String publisher;

    public Post(String postid, String postimage, String description, String publisher) {
        Postid = postid;
        this.postimage = postimage;
        this.description = description;
        this.publisher = publisher;
    }
    public Post() {

    }

    public String getPostid() {
        return Postid;
    }

    public void setPostid(String postid) {
        Postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPsotimage(String psotimage) {
        this.postimage = psotimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
