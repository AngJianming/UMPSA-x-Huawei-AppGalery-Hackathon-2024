package com.mobile_prog.habit_quest.models;

import java.io.Serializable;

public class UserQuest implements Serializable {

    private String id;
    private String userQuestTypeId;
    private String questId;
    private boolean isDone;

    public UserQuest(String id, String userQuestTypeId, String questId, boolean isDone) {
        this.id = id;
        this.userQuestTypeId = userQuestTypeId;
        this.questId = questId;
        this.isDone = isDone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserQuestTypeId() {
        return userQuestTypeId;
    }

    public void setUserQuestTypeId(String userQuestTypeId) {
        this.userQuestTypeId = userQuestTypeId;
    }

    public String getQuestId() {
        return questId;
    }

    public void getQuestId(String questId) {
        this.questId = questId;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
