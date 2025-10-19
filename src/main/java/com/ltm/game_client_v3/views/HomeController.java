package com.ltm.game_client_v3.views;

import com.ltm.game_client_v3.controller.ClientManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class HomeController {
    private ClientManager clientManager;

    @FXML private Label welcomeLabel;
    @FXML private Button soundButton;

    private boolean soundOn = true;

    public void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @FXML
    private void initialize() {
    }

    @FXML
    private void onSoundToggle() {
        soundOn = !soundOn;
        soundButton.setText(soundOn ? "🔊" : "🔇");
    }

    @FXML
    private void onPlay() {
    }

    @FXML
    private void onLogout() {
    }


}