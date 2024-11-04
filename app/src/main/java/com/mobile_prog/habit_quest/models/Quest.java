package com.mobile_prog.habit_quest.models;

import java.io.Serializable;

public class Quest implements Serializable {
    private String id;
    private QuestType type;
    private String questTypeId;
    private String name;
    private String description;
    private String tips;

    public Quest(String id, String questTypeId, String name, String description, String tips) {
        this.id = id;
        this.questTypeId = questTypeId;
        this.name = name;
        this.description = description;
        this.tips = tips;
    }

    public Quest() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public QuestType getType() {
        return type;
    }

    public void setType(QuestType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getQuestTypeId() {
        return questTypeId;
    }

    public void setQuestTypeId(String questTypeId) {
        this.questTypeId = questTypeId;
    }
}
