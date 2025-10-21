package com.ltm.game_client_v3.views;

import com.ltm.game_client_v3.controller.ClientManager;
import com.ltm.game_client_v3.controller.SoundManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONObject;

public class HomeController {
    private ClientManager clientManager;

    @FXML private Label welcomeLabel;
    @FXML private Button soundButton;

    @FXML
    private ImageView soundIcon;
    private Image soundOnImg;
    private Image soundOffImg;

    private boolean soundOn = true;

    public void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @FXML
    public void initialize() {
        SoundManager.playBackgroundMusic();
        soundOnImg = new Image(getClass().getResource("/images/sound-on.png").toExternalForm());
        soundOffImg = new Image(getClass().getResource("/images/sound-off.png").toExternalForm());

        soundIcon = new ImageView(soundOnImg);
        soundIcon.setFitWidth(32);
        soundIcon.setFitHeight(32);

        soundButton.setGraphic(soundIcon);
        soundButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        soundButton.setOnAction(e -> onSoundToggle());
    }

    @FXML
    private void onSoundToggle() {
        SoundManager.toggleMute();
        boolean muted = SoundManager.isMuted();
        soundIcon.setImage(muted ? soundOffImg : soundOnImg);
    }

    @FXML
    private void onPlay() {
    }

    @FXML
    private void onLogout() {
        JSONObject msg = new JSONObject();
        msg.put("action", "LOGOUT_REQUEST");
        clientManager.send(msg);
        SoundManager.stopMusic();
        clientManager.getViewManager().showAuth();
    }
}