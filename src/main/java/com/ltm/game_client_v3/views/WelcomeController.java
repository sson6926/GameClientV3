package com.ltm.game_client_v3.views;

import com.ltm.game_client_v3.controller.ClientManager;
import com.ltm.game_client_v3.controller.SoundManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class WelcomeController {
    private ClientManager clientManager;

    @FXML private Label welcomeLabel;
    @FXML private Button soundButton;

    @FXML
    private ImageView soundIcon;
    private Image soundOnImg;
    private Image soundOffImg;

    private boolean soundOn = true;
    @FXML
    private MediaView backgroundVideo;

    private MediaPlayer mediaPlayer;


    public void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @FXML
    public void initialize() {
            SoundManager.playBackgroundMusic();
            
            // Load sound icons
            soundOnImg = new Image(getClass().getResource("/images/sound-on.png").toExternalForm());
            soundOffImg = new Image(getClass().getResource("/images/sound-off.png").toExternalForm());

            soundIcon = new ImageView(soundOnImg);
            soundIcon.setFitWidth(32);
            soundIcon.setFitHeight(32);

            soundButton.setGraphic(soundIcon);
            soundButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            soundButton.setOnAction(e -> onSoundToggle());
            
            initializeVideo();
    }

    private void initializeVideo() {
        try {
            String videoPath = getClass().getResource("/images/video_bg.mp4").toExternalForm();
            Media media = new Media(videoPath);
            mediaPlayer = new MediaPlayer(media);
            
            // Đợi video buffer đủ trước khi play
            mediaPlayer.setOnReady(() -> {
                System.out.println("Video buffered, starting smooth playback");
                mediaPlayer.play();
            });
            
            // Buffer trước toàn bộ video
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(0);
            mediaPlayer.setRate(1.0); // Tốc độ bình thường
            
            mediaPlayer.setOnReady(() -> {
                mediaPlayer.play();
            });

            
            mediaPlayer.setOnError(() -> {
                System.err.println("Video error: " + mediaPlayer.getError());
            });
            
            backgroundVideo.setMediaPlayer(mediaPlayer);
            
        } catch (Exception e) {
            System.err.println("Video initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void onSoundToggle() {
        SoundManager.toggleMute();
        boolean muted = SoundManager.isMuted();
        soundIcon.setImage(muted ? soundOffImg : soundOnImg);
    }

    @FXML
    private void onPlay() {
        
        clientManager.getViewManager().showHome();
    }

    @FXML
    private void onLogout() {
    }


}