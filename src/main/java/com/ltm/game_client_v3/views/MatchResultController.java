package com.ltm.game_client_v3.views;

import com.ltm.game_client_v3.controller.ClientManager;
import com.ltm.game_client_v3.controller.SoundManager;
import com.ltm.game_client_v3.models.MatchSummary;
import com.ltm.game_client_v3.models.User;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.net.URL;
import java.util.ResourceBundle;

public class MatchResultController implements Initializable {

    private ClientManager clientManager;
    private User currentUser;
    private MatchSummary matchSummary;

    @FXML private MediaView backgroundVideo;
    @FXML private Button soundButton;
    @FXML private ImageView soundIcon;
    @FXML private Label resultEmoji;
    @FXML private Label resultTitle;
    @FXML private Label playerName;
    @FXML private Label playerWins;
    @FXML private Label playerTotalScore;
    @FXML private Label opponentName;
    @FXML private Label opponentWins;
    @FXML private Label opponentTotalScore;
    @FXML private Label totalWinsLabel;
    @FXML private Button playAgainButton;
    @FXML private Button shareButton;

    private MediaPlayer mediaPlayer;
    private Image soundOnImg;
    private Image soundOffImg;

    public void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void setMatchSummary(MatchSummary summary) {
        this.matchSummary = summary;
        updateResultDisplay();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupSound();
        initializeVideo();
     
    }

    private void setupSound() {
        // Load sound icons
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

    private void initializeVideo() {
        try {
            String videoPath = getClass().getResource("/images/video_bg.mp4").toExternalForm();
            Media media = new Media(videoPath);
            mediaPlayer = new MediaPlayer(media);
            
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(0);
            
            mediaPlayer.setOnReady(() -> {
                mediaPlayer.play();
            });

            mediaPlayer.setOnError(() -> {
                System.err.println("Video error: " + mediaPlayer.getError());
            });
            
            backgroundVideo.setMediaPlayer(mediaPlayer);
            
        } catch (Exception e) {
            System.err.println("Video initialization failed: " + e.getMessage());
        }
    }


    private void updateResultDisplay() {
        if (matchSummary == null || currentUser == null) return;

        int currentUserId = currentUser.getId();
        int myWins = matchSummary.getMyWins(currentUserId);
        int opponentWins2 = matchSummary.getOpponentWins(currentUserId);
        int myTotalScore = matchSummary.getMyTotalScore(currentUserId);
        int opponentTotalScore2 = matchSummary.getOpponentTotalScore(currentUserId);

        // Determine result
        String resultText;
        String resultEmojiText;
        String resultStyle;

        if (myWins > opponentWins2) {
            resultText = "VICTORY!";
            resultEmojiText = "🏆";
            resultStyle = "victory";
        } else if (myWins < opponentWins2) {
            resultText = "DEFEAT";
            resultEmojiText = "💔";
            resultStyle = "defeat";
        } else {
            if (myTotalScore > opponentTotalScore2) {
                resultText = "VICTORY!";
                resultEmojiText = "🏆";
                resultStyle = "victory";
            } else if (myTotalScore < opponentTotalScore2) {
                resultText = "DEFEAT";
                resultEmojiText = "💔";
                resultStyle = "defeat";
            } else {
                resultText = "DRAW";
                resultEmojiText = "🤝";
                resultStyle = "draw";
            }
        }

        // Update UI
        resultEmoji.setText(resultEmojiText);
        resultTitle.setText(resultText);
        resultTitle.getStyleClass().setAll("result-title", resultStyle);

        playerName.setText("YOU");
        playerWins.setText(myWins + " Win" + (myWins != 1 ? "s" : ""));
        playerTotalScore.setText("Total: " + myTotalScore);

        opponentName.setText("OPPONENT");
        opponentWins.setText(opponentWins2 + " Win" + (opponentWins2 != 1 ? "s" : ""));
        opponentTotalScore.setText("Total: " + opponentTotalScore2);

        totalWinsLabel.setText(myWins + " - " + opponentWins2);

    
    }



    @FXML
    private void onBackToHome() {
        SoundManager.stopMusic();
        clientManager.getViewManager().showHome();
    }

    @FXML
    private void onPlayAgain() {
        // Gửi yêu cầu chơi lại hoặc tìm đối thủ mới
        // clientManager.sendPlayAgainRequest();
        // Hiện tại quay về Home
        onBackToHome();
    }

    @FXML
    private void onShareResult() {
        // Hiển thị dialog chia sẻ kết quả
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Share Result");
        alert.setHeaderText("Share Feature");
        alert.setContentText("Share functionality will be implemented in future updates!");
        alert.showAndWait();
    }
}