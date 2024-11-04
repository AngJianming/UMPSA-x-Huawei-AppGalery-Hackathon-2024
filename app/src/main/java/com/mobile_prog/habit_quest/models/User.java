package com.mobile_prog.habit_quest.models;

public class User {
    private String id;
    private String username;
    private String email;
    private int exp;
    private int level;
    private String avatarUri;

    public User(String id, String username, String email, int exp, int level, String avatarUri) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.exp = exp;
        this.level = level;
        this.avatarUri = avatarUri;
    }

    public User() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }
}
