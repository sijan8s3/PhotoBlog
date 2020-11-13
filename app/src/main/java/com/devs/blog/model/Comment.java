package com.devs.blog.model;

public class Comment {
    String comment, authorID, commentID;

    public Comment() {
    }

    public Comment(String comment, String authorID, String commentID) {
        this.comment = comment;
        this.authorID = authorID;
        this.commentID = commentID;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }
}
