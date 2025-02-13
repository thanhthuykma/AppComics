package com.example.appcomics.Model;
public class Comment {
    private int id;
    private String username;
    private String comment;
    private String created_at;
    private String reply_user;
    private String reply_comment;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getReply_user() {
        return reply_user;
    }

    public void setReply_user(String reply_user) {
        this.reply_user = reply_user;
    }

    public String getReply_comment() {
        return reply_comment;
    }

    public void setReply_comment(String reply_comment) {
        this.reply_comment = reply_comment;
    }
}
