package com.quiz.quizera.utils;

import com.quiz.quizera.models.User;

public class UserSession {
    private static User current;

    public static void set(User user) { current = user; }
    public static User get() { return current; }
    public static int requireUserId() { return current != null ? current.getId() : -1; }
    public static void clear() { current = null; }
}
