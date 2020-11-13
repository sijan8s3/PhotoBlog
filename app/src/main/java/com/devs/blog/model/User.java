package com.devs.blog.model;

public class User {
    private String Uid, bio, email, imageurl, name, username;

    public User() {
    }

    public User(String uid, String bio, String email, String imageurl, String name, String username) {
        Uid = uid;
        this.bio = bio;
        this.email = email;
        this.imageurl = imageurl;
        this.name = name;
        this.username = username;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
