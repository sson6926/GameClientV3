package com.ltm.game_client_v3.controller;

import com.ltm.game_client_v3.views.AuthController;
import com.ltm.game_client_v3.views.HomeController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewManager {
    private final Stage primaryStage;
    private HomeController homeController;
    private AuthController authController;
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

//    public void showLogin() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
//            Scene scene = new Scene(loader.load());
//            loginController = loader.getController();
//            loginController.setClientManager(clientManager);
//            primaryStage.setScene(scene);
//            primaryStage.setTitle("Login");
//            primaryStage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void showHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/HomeView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Platform.runLater(() -> {
                primaryStage.setScene(scene);
                primaryStage.setTitle("Home");
                primaryStage.show();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public AuthController getAuthController() { return authController; }
    // public HomeController getHomeController() { return homeController; }
}