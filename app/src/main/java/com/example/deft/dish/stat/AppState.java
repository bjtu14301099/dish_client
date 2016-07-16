package com.example.deft.dish.stat;

import com.example.deft.dish.modal.User;

/**
 * Created by niezeshu on 16/7/16.
 */
public class AppState {
    private static AppState ourInstance = new AppState();

    public static AppState getInstance() {
        return ourInstance;
    }

    private User user;

    private AppState() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
