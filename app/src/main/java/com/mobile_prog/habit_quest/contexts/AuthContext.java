package com.mobile_prog.habit_quest.contexts;

import com.mobile_prog.habit_quest.models.User;

public class AuthContext {
    private static User current = null;

    public static String getName() {
        if (current == null) return "";
        return current.getUsername();
    }

    public static String getAvatarUriString() {
        if (current == null) return "";
        return current.getAvatarUri();
    }

    public static String getEmail() {
        if (current == null) return "";
        return current.getEmail();
    }

    public static String getId() {
        if (current == null) return "";
        return current.getId();
    }

    public static void set(User user) {
        current = user;
    }

    public static Integer getLevel() {
        if (current == null) return -1;
        return current.getLevel();
    }

    public static Integer getExp() {
        if (current == null) return -1;
        return current.getExp();
    }
}
