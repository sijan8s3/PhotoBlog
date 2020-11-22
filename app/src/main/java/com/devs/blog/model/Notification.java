package com.devs.blog.model;

public class Notification {
    private String userID, text, postID;
    private boolean isPost;

    public Notification() {
    }

    public Notification(String userID, String text, String postID, boolean isPost) {
        this.userID = userID;
        this.text = text;
        this.postID = postID;
        this.isPost = isPost;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }
}
