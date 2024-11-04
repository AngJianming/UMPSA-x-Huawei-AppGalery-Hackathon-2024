package com.mobile_prog.habit_quest.models;

public class UserQuestType {
    private String id;
    private String questTypeId;
    private String userId;
    private boolean isDone;

    public UserQuestType(String id, String questTypeId, String userId, boolean isDone) {
        this.id = id;
        this.questTypeId = questTypeId;
        this.userId = userId;
        this.isDone = isDone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestTypeId() {
        return questTypeId;
    }

    public void setQuestTypeId(String questTypeId) {
        this.questTypeId = questTypeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
