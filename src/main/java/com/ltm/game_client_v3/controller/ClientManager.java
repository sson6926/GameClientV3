package com.ltm.game_client_v3.controller;

import javafx.stage.Stage;
import org.json.JSONObject;

public class ClientManager {
    private static ClientManager instance;
    private SocketManager socketManager;
    private UserManager userManager;
    private ViewManager viewManager;
    private MessageHandler messageHandler;

    private ClientManager() {}

    public static ClientManager getInstance() {
        if (instance == null) instance = new ClientManager();
        return instance;
    }

    // start nhận Stage từ JavaFX Application
    public void start(Stage primaryStage) {
        socketManager = new SocketManager("172.30.34.82", 8989, this::onMessageReceived);
//        socketManager = new SocketManager("localhost", 8989, this::onMessageReceived);
        System.out.println("Starting client...");
        userManager = new UserManager();
        viewManager = new ViewManager(this, primaryStage);
        messageHandler = new MessageHandler(this, userManager, viewManager);

        new Thread(socketManager).start();
        viewManager.showAuth();
//        viewManager.showHome();
    }

    private void onMessageReceived(JSONObject msg) {
        if (messageHandler != null) {
            messageHandler.handleMessage(msg);
        }
    }

    public SocketManager getSocketManager() { return socketManager; }
    public UserManager getUserManager() { return userManager; }
    public void setUserManager(UserManager userManager) { this.userManager = userManager; }
    public ViewManager getViewManager() { return viewManager; }
    public void send(JSONObject msg) { 
        if (socketManager != null) socketManager.send(msg); 
    }
}