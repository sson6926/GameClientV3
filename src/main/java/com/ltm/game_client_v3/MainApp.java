package com.ltm.game_client_v3;

import com.ltm.game_client_v3.controller.ClientManager;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.text.Font;


public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        Font.loadFont(getClass().getResource("/fonts/FreckleFace-Regular.ttf").toExternalForm(),
                10
        );
        ClientManager.getInstance().start(primaryStage);
    }
    public static void main(String[] args) {
        launch(args);
    }
}