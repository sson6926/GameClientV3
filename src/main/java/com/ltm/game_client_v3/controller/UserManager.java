package com.ltm.game_client_v3.controller;

import com.ltm.game_client_v3.models.User;

public class UserManager {
    private User currentUser;

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { this.currentUser = user; }
}