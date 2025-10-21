package com.ltm.game_client_v3.controller;

import com.ltm.game_client_v3.models.User;
import com.ltm.game_client_v3.views.AuthController;
import org.json.JSONObject;

public class MessageHandler {
    private final ClientManager clientManager;
    private final UserManager userManager;
    private final ViewManager viewManager;

    public MessageHandler(ClientManager clientManager, UserManager userManager, ViewManager viewManager) {
        this.clientManager = clientManager;
        this.userManager = userManager;
        this.viewManager = viewManager;
    }

    public void handleMessage(JSONObject msg) {
        System.out.println("Received message: " + msg.toString());
        String action = msg.optString("action");
        switch (action) {
            case "LOGIN_RESPONSE" -> {
                String status = msg.optString("status");
                if ("success".equals(status)) {
                    JSONObject userData = msg.optJSONObject("user");
                    System.out.println("Login successful for user: " +  userData.optString("nickname"));
                    userManager.setCurrentUser(new User(userData.optString("nickname")));
                    viewManager.showHome();
                } else {
                    AuthController authController = viewManager.getAuthController();
                    authController.showLoginError(msg.optString("message"));
                }
            }
            case "REGISTER_RESPONSE" -> {
                String status = msg.optString("status");
                AuthController authController = viewManager.getAuthController();
                if ("success".equals(status)) {
                    if (authController != null)
                        authController.showNotify(msg.optString("message"));
                        authController.showLogin();
                } else {
                    if (authController != null)
                        authController.showRegisterError(msg.optString("message"));
                }
//
            }
            default -> System.out.println("Unknown message type: " + action);
        }
    }
}