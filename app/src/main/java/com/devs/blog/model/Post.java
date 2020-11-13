package com.devs.blog.model;

public class Post {
    String description, imageUrl, postID, creator;

    public Post() {
    }

    public Post(String description, String imageUrl, String postID, String creator) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.postID = postID;
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
