package com.mobile_prog.habit_quest.models;

public class QuestType {

    private String id;
    private String name;
    private String description;
    private int day;

    public QuestType(String id, String name, String description, int day) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.day = day;
    }

    public QuestType() {};

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
