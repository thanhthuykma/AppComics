package com.example.appcomics.Model;
public class Comment {
    private int id;
    private int manga_id;
    private String username;
    private String comment;
    private Integer parent_id;
    private String created_at;
    private int like_count;

    public Comment(int id, int manga_id, String username, String comment, Integer parent_id, String created_at, int like_count) {
        this.id = id;
        this.manga_id = manga_id;
        this.username = username;
        this.comment = comment;
        this.parent_id = parent_id;
        this.created_at = created_at;
        this.like_count = like_count;
    }

    public Comment(int manga_id, String username, String comment, Integer parent_id) {
        this.manga_id = manga_id;
        this.username = username;
        this.comment = comment;
        this.parent_id = parent_id;
    }
    // Getters and setters

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getManga_id() {
        return manga_id;
    }

    public void setManga_id(int manga_id) {
        this.manga_id = manga_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }
}
