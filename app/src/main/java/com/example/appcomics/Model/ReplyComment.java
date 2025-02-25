package com.example.appcomics.Model;

public class ReplyComment {
    private int manga_id;
    private String reply_user ;
    private String reply_comment;
    private Integer reply_parentid;
    private int reply_like;
    private int reply_id;

    public ReplyComment(int manga_id,String reply_user, String reply_comment, Integer reply_parentid, int reply_like) {
        this.manga_id = manga_id;
        this.reply_user = reply_user;
        this.reply_comment = reply_comment;
        this.reply_parentid = reply_parentid;
        this.reply_like = reply_like;
    }

    public int getManga_id() {
        return manga_id;
    }

    public void setManga_id(int manga_id) {
        this.manga_id = manga_id;
    }

    public int getReply_like() {
        return reply_like;
    }

    public void setReply_like(int reply_like) {
        this.reply_like = reply_like;
    }

    public int getReply_id() {
        return reply_id;
    }

    public void setReply_id(int reply_id) {
        this.reply_id = reply_id;
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

    public Integer getReply_parentid() {
        return reply_parentid;
    }

    public void setReply_parentid(Integer reply_parentid) {
        this.reply_parentid = reply_parentid;
    }
}
