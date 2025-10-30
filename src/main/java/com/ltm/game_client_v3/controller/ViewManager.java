package com.ltm.game_client_v3.controller;

import com.ltm.game_client_v3.models.GameData;
import com.ltm.game_client_v3.models.User;
import com.ltm.game_client_v3.views.AuthController;
import com.ltm.game_client_v3.views.GameSortingController;
import com.ltm.game_client_v3.views.HomeController;
import com.ltm.game_client_v3.views.WelcomeController;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewManager {
    private final Stage primaryStage;
    private AuthController authController;
    private HomeController homeController;
    private GameSortingController gameSortingController;
    private final ClientManager clientManager;

    public ViewManager(ClientManager clientManager, Stage primaryStage) {
        this.clientManager = clientManager;
        this.primaryStage = primaryStage;
    }

    public void showAuth() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AuthView.fxml"));
            Scene scene = new Scene(loader.load());

            authController = loader.getController();
            authController.setClientManager(clientManager);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Game Portal");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showGamePlay(GameData gameData) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GameSortingView.fxml"));
            Scene scene = new Scene(loader.load());

            gameSortingController = loader.getController();
            gameSortingController.setClientManager(clientManager);
            gameSortingController.setGameData(gameData);
            
            Platform.runLater(() -> {
                primaryStage.setScene(scene);
                primaryStage.setTitle("Sorting Game - Match " + gameData.getMatchId());
                primaryStage.show();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/HomeView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            homeController = loader.getController();
            homeController.setClientManager(clientManager);
                // Debug current user
            User currentUser = clientManager.getUserManager().getCurrentUser();
            System.out.println("ViewManager - Current user: " + 
                (currentUser != null ? currentUser.getNickname() : "NULL"));
            homeController.setCurrentUser(currentUser);
            requestOnlineUsers();

            Platform.runLater(() -> {
                primaryStage.setScene(scene);
                primaryStage.setTitle("Home");
                primaryStage.show();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void requestOnlineUsers() {
        org.json.JSONObject requestJson = new org.json.JSONObject();
        requestJson.put("action", "GET_ONLINE_USERS");
        clientManager.send(requestJson);
    }

    public void showWelcome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/WelcomeView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            WelcomeController welcomeController = loader.getController();
            welcomeController.setClientManager(clientManager);
            Platform.runLater(() -> {
                primaryStage.setScene(scene);
                primaryStage.setTitle("Welcome");
                primaryStage.show();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public AuthController getAuthController() { return authController; }
    public HomeController getHomeController() { return homeController; }
    public GameSortingController getGameSortingController() { return gameSortingController; }
}