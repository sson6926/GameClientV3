package com.ltm.game_client_v3.views;

import com.ltm.game_client_v3.controller.ClientManager;
import com.ltm.game_client_v3.controller.SoundManager;
import com.ltm.game_client_v3.models.MatchSummary;
import com.ltm.game_client_v3.models.User;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.css.Match;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

public class HomeController implements Initializable {

    private ClientManager clientManager;
    private User currentUser;

    @FXML private Label welcomeLabel;
    @FXML private Button playButton;
    @FXML private Button logoutButton;
    @FXML private TextArea messageArea;
    @FXML private ListView<User> onlinePlayersList;
    @FXML private Button soundButton;
    @FXML
    private ImageView soundIcon;
    private Image soundOnImg;
    private Image soundOffImg;
     @FXML
    private MediaView backgroundVideo;
      @FXML
    private ImageView backgroundImage;

    private MediaPlayer mediaPlayer;

    private boolean soundOn = true;

    // Context menu và dialog management
    private ContextMenu playerContextMenu;
    private Dialog<Void> currentWaitingDialog;
    private Alert currentInviteDialog;
    private String currentInviteTarget; // Lưu username đang mời

    public void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateWelcomeMessage();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupSound();
        setupMessageArea();
        setupPlayersList();
        setupContextMenu();
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
                showBackgroundImage();
            });
            
            backgroundVideo.setMediaPlayer(mediaPlayer);
            
        } catch (Exception e) {
            System.err.println("Video initialization failed: " + e.getMessage());
            e.printStackTrace();
            showBackgroundImage();
        }
    }
    private void showBackgroundImage() {
        try {
            // Load ảnh nền thay thế
            Image bgImage = new Image(getClass().getResource("/images/login-bg.jpg").toExternalForm());
            
            // Nếu có ImageView từ FXML, sử dụng nó
            if (backgroundImage != null) {
                backgroundImage.setImage(bgImage);
                backgroundImage.setVisible(true);
            } else {
                // Nếu không có, tạo mới và thêm vào scene
                backgroundImage = new ImageView(bgImage);
                backgroundImage.setPreserveRatio(true);
                backgroundImage.setFitWidth(backgroundVideo.getScene().getWidth());
                backgroundImage.setFitHeight(backgroundVideo.getScene().getHeight());
                
                // Thêm ảnh nền vào cùng container với video
                if (backgroundVideo.getParent() != null) {
                    ((javafx.scene.Parent) backgroundVideo.getParent()).getChildrenUnmodifiable().add(backgroundImage);
                }
            }
            
            // Ẩn video
            backgroundVideo.setVisible(false);
            System.out.println("Showing background image instead of video");
            
        } catch (Exception e) {
            System.err.println("Failed to load background image: " + e.getMessage());

        }
    }

    private void setupSound() {
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
    }
    @FXML
    private void onSoundToggle() {
        SoundManager.toggleMute();
        boolean muted = SoundManager.isMuted();
        soundIcon.setImage(muted ? soundOffImg : soundOnImg);
    }

    private void setupMessageArea() {
        messageArea.setEditable(false);
        addMessage("Welcome to the Game Portal!");
        addMessage("Click 'Quick Play' to start a game or invite players from the list.");
    }

    private void setupPlayersList() {
        onlinePlayersList.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User player, boolean empty) {
                super.updateItem(player, empty);
                
                if (empty || player == null) {
                    setText(null);
                    getStyleClass().removeAll("status-online", "status-playing", "status-offline");
                } else {
                    String displayText = player.getNickname();
                    
                    if (player.isPlaying()) {
                        displayText += " (In Game)";
                        getStyleClass().add("status-playing");
                    } else if (!player.isOnline()) {
                        displayText += " (Offline)";
                        getStyleClass().add("status-offline");
                    } else {
                        getStyleClass().add("status-online");
                    }
                    
                    setText(displayText);
                    
                    Tooltip tooltip = new Tooltip(
                        "Username: " + player.getUsername() + "\n" +
                        "Status: " + (player.isPlaying() ? "In Game" : 
                                    player.isOnline() ? "Online" : "Offline")
                    );
                    setTooltip(tooltip);
                }
            }
        });

        onlinePlayersList.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                User selectedPlayer = onlinePlayersList.getSelectionModel().getSelectedItem();
                if (selectedPlayer != null && !selectedPlayer.isPlaying() && selectedPlayer.isOnline()) {
                    invitePlayer(selectedPlayer.getUsername(), selectedPlayer.getNickname());
                }
            }
        });
    }

    private void setupContextMenu() {
        playerContextMenu = new ContextMenu();
        
        MenuItem inviteItem = new MenuItem("Invite to Game");
        inviteItem.setOnAction(event -> {
            User selectedPlayer = onlinePlayersList.getSelectionModel().getSelectedItem();
            if (selectedPlayer != null) {
                invitePlayer(selectedPlayer.getUsername(), selectedPlayer.getNickname());
            }
        });
        
        MenuItem profileItem = new MenuItem("View Profile");
        profileItem.setOnAction(event -> {
            User selectedPlayer = onlinePlayersList.getSelectionModel().getSelectedItem();
            if (selectedPlayer != null) {
                viewProfile(selectedPlayer.getNickname());
            }
        });
        
        MenuItem messageItem = new MenuItem("Send Message");
        messageItem.setOnAction(event -> {
            User selectedPlayer = onlinePlayersList.getSelectionModel().getSelectedItem();
            if (selectedPlayer != null) {
                sendMessage(selectedPlayer.getNickname());
            }
        });
        
        playerContextMenu.getItems().addAll(inviteItem, profileItem, new SeparatorMenuItem(), messageItem);

        onlinePlayersList.setContextMenu(playerContextMenu);
        
        onlinePlayersList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                boolean canInteract = !newVal.isPlaying() && newVal.isOnline();
                inviteItem.setDisable(!canInteract);
                messageItem.setDisable(!canInteract);
            }
        });
    }

    private void updateWelcomeMessage() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getNickname() + "!");
        }
    }

    @FXML
    private void onScoreboard() {
        addMessage("Scoreboard clicked");
        JSONObject data = new JSONObject();
        data.put("action", "GET_RANKING_REQUEST");
        clientManager.send(data);

    }

    public void openScoreboardPopup(JSONArray rankingArray) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ScoreboardView.fxml"));
            Parent root = loader.load();

            // Lấy controller để truyền dữ liệu vào
            ScoreboardController controller = loader.getController();
            controller.setRanking(rankingArray);

            Stage stage = new Stage();
            stage.setTitle("Scoreboard");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onHistory() {
        addMessage("History clicked");
    }



    @FXML
    private void onPlay() {
        addMessage("Finding opponents...");
    }

    @FXML
    private void onLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Confirm Logout");
        alert.setContentText("Are you sure you want to logout?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                addMessage("Logging out...");

                //gửi message logout lên server
                JSONObject logoutMsg = new JSONObject();
                logoutMsg.put("action", "LOGOUT_REQUEST");
                clientManager.send(logoutMsg);

                JSONObject requestJson = new org.json.JSONObject();
                requestJson.put("action", "GET_ONLINE_USERS");
                clientManager.send(requestJson);

                //xóa user hiện tại
                clientManager.getUserManager().setCurrentUser(null);

                SoundManager.stopMusic();

                //đóng socket nếu muốn
                // clientManager.getSocketManager().stop();

                //chuyển về màn hình đăng nhập
                clientManager.getViewManager().showAuth();
            }
        });
    }

    public void addMessage(String message) {
        Platform.runLater(() -> {
            messageArea.appendText(message + "\n");
            messageArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    private void invitePlayer(String username, String nickname) {
        addMessage("Sending invitation to " + nickname + "...");
        
        // Đóng dialog cũ nếu có
        closeWaitingDialog();
         // Lưu thông tin người đang mời
        currentInviteTarget = username;
        
        showWaitingDialog("Waiting for " + nickname + " to respond...");

        JSONObject inviteMsg = new JSONObject();
        inviteMsg.put("action", "INVITE_USER_TO_GAME");
        inviteMsg.put("targetUsername", username);
        clientManager.send(inviteMsg);
    }

 private void viewProfile(String nickname) {
        // Tìm user được chọn từ danh sách
        User selectedPlayer = onlinePlayersList.getItems().stream()
                .filter(user -> user.getNickname().equals(nickname))
                .findFirst()
                .orElse(null);
        
        if (selectedPlayer == null) {
            return;
        }
        
        Platform.runLater(() -> {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Player Profile");
            dialog.setHeaderText(null);

            // Tính win rate
            double winRate = 0.0;
            if (selectedPlayer.getTotalMatches() > 0) {
                winRate = (double) selectedPlayer.getTotalWins() / selectedPlayer.getTotalMatches() * 100;
            }

            // Tạo giao diện profile
            VBox content = new VBox(15);
            content.setPadding(new Insets(20));
            content.setAlignment(Pos.TOP_CENTER);
            content.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%); -fx-background-radius: 15;");

            // Avatar/Icon
            ImageView avatar = new ImageView(new Image(getClass().getResource("/images/avatar.png").toExternalForm()));
            avatar.setFitWidth(80);
            avatar.setFitHeight(80);
            avatar.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");

            // Tên người chơi
            Label nameLabel = new Label(selectedPlayer.getNickname());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 0, 1);");

            // Username
            Label usernameLabel = new Label("@" + selectedPlayer.getUsername());
            usernameLabel.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 14px;");

            // Status
            HBox statusBox = new HBox(10);
            statusBox.setAlignment(Pos.CENTER);
            
            Label statusLabel = new Label();
            statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10 5 10; -fx-background-radius: 10;");
            
            if (selectedPlayer.isPlaying()) {
                statusLabel.setText("🎮 IN GAME");
                statusLabel.setStyle(statusLabel.getStyle() + "-fx-background-color: #ff6b6b;");
            } else if (selectedPlayer.isOnline()) {
                statusLabel.setText("🟢 ONLINE");
                statusLabel.setStyle(statusLabel.getStyle() + "-fx-background-color: #51cf66;");
            } else {
                statusLabel.setText("⚫ OFFLINE");
                statusLabel.setStyle(statusLabel.getStyle() + "-fx-background-color: #868e96;");
            }

            statusBox.getChildren().add(statusLabel);

            // Thống kê - Grid layout
            GridPane statsGrid = new GridPane();
            statsGrid.setHgap(15);
            statsGrid.setVgap(10);
            statsGrid.setAlignment(Pos.CENTER);
            statsGrid.setPadding(new Insets(10));

            // Các chỉ số thống kê
            String[] statLabels = {"📊 Total Matches", "🏆 Total Wins", "⭐ Total Score", "📈 Win Rate"};
            String[] statValues = {
                String.valueOf(selectedPlayer.getTotalMatches()),
                String.valueOf(selectedPlayer.getTotalWins()),
                String.valueOf(selectedPlayer.getTotalScore()),
                String.format("%.1f%%", winRate)
            };

            for (int i = 0; i < statLabels.length; i++) {
                Label label = new Label(statLabels[i]);
                label.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 12px;");
                
                Label value = new Label(statValues[i]);
                value.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
                
                statsGrid.add(label, 0, i);
                statsGrid.add(value, 1, i);
            }

            // Progress bar cho win rate (thêm visual)
            ProgressBar winRateBar = new ProgressBar(winRate / 100);
            winRateBar.setPrefWidth(200);
            winRateBar.setStyle("-fx-accent: " + getWinRateColor(winRate) + "; -fx-pref-height: 8px;");
            
            Label winRateText = new Label(String.format("Win Rate: %.1f%%", winRate));
            winRateText.setStyle("-fx-text-fill: white; -fx-font-size: 11px;");

            VBox winRateBox = new VBox(5, winRateBar, winRateText);
            winRateBox.setAlignment(Pos.CENTER);

            // Assemble content
            content.getChildren().addAll(avatar, nameLabel, usernameLabel, statusBox, new Separator(), statsGrid, winRateBox);

            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            
            // Style cho dialog pane
            dialog.getDialogPane().setStyle("-fx-background-color: transparent; -fx-border-color: #495057; -fx-border-width: 2; -fx-border-radius: 15;");
            
            // Ẩn nút Close mặc định và thay bằng custom button
            Button closeButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
            closeButton.setText("Close");
            closeButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 8 20 8 20;");

            dialog.showAndWait();
        });
    }
        // Helper method để chọn màu cho win rate
    private String getWinRateColor(double winRate) {
        if (winRate >= 70) return "#51cf66"; // Xanh lá - Cao
        if (winRate >= 50) return "#ffd43b"; // Vàng - Trung bình
        if (winRate >= 30) return "#ff922b"; // Cam - Thấp
        return "#ff6b6b"; // Đỏ - Rất thấp
    }

    private void sendMessage(String playerName) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Send Message");
        dialog.setHeaderText("Send message to " + playerName);
        dialog.setContentText("Message:");
        
        dialog.showAndWait().ifPresent(message -> {
            if (!message.trim().isEmpty()) {
                addMessage("To " + playerName + ": " + message);
            }
        });
    }

    public void updateOnlinePlayersList(List<User> players) {
        Platform.runLater(() -> {
            onlinePlayersList.getItems().setAll(players);
        });
    }

    public void showInviteDialog(String inviterUsername, String inviterNickname) {
        Platform.runLater(() -> {
            // Đóng dialog cũ nếu có
            closeCurrentDialogs();
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Game Invitation");
            alert.setHeaderText("Game Invitation Received!");
            alert.setContentText(inviterNickname + " invited you to play a game!\nDo you want to accept?");
            
            ButtonType acceptButton = new ButtonType("Accept");
            ButtonType rejectButton = new ButtonType("Reject");
            alert.getButtonTypes().setAll(acceptButton, rejectButton);
            
            // Lưu reference để có thể đóng sau
            currentInviteDialog = alert;
            
            alert.showAndWait().ifPresent(response -> {
                boolean accepted = response == acceptButton;
                
                JSONObject responseJson = new JSONObject();
                responseJson.put("action", "INVITE_USER_TO_GAME_RESPONSE");
                responseJson.put("inviterUsername", inviterUsername);
                responseJson.put("accepted", accepted);
                clientManager.send(responseJson);
                
                addMessage("You " + (accepted ? "accepted" : "declined") + " " + inviterNickname + "'s invitation.");
                currentInviteDialog = null; // Clear reference
            });
        });
    }

   private void showWaitingDialog(String message) {
        Platform.runLater(() -> {
            
            Dialog<Void> waitingDialog = new Dialog<>();
            waitingDialog.setTitle("Waiting for Response");
            waitingDialog.setHeaderText(message);
            
            ProgressIndicator progress = new ProgressIndicator();
            VBox content = new VBox(20, progress);
            content.setAlignment(javafx.geometry.Pos.CENTER);
            content.setPadding(new javafx.geometry.Insets(20));
            
            waitingDialog.getDialogPane().setContent(content);
            
            // CHỈ THÊM NÚT CANCEL, KHÔNG TỰ ĐÓNG
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            waitingDialog.getDialogPane().getButtonTypes().add(cancelButton);
            
            // Xử lý khi cancel
            waitingDialog.setResultConverter(dialogButton -> {
                if (dialogButton == cancelButton) {
                    addMessage("Invitation cancelled.");
                    // Gửi message hủy invite tới server (nếu cần)
                    JSONObject cancelMsg = new JSONObject();
                    cancelMsg.put("action", "CANCEL_INVITE");
                    cancelMsg.put("targetUsername", currentInviteTarget);
                    clientManager.send(cancelMsg);
                }
                return null;
            });
            
            currentWaitingDialog = waitingDialog;
            waitingDialog.show();
        });
    }

    public void showInviteResult(String targetNickname, boolean accepted) {
        Platform.runLater(() -> {
            // Đóng waiting dialog khi có kết quả
            closeWaitingDialog();
            
            String message = targetNickname + (accepted ? " accepted your invitation!" : " declined your invitation.");
            addMessage(message);
            
            if (accepted) {
                // Nếu được chấp nhận, hiển thị thông báo và chờ server START_GAME
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Invitation Accepted");
                alert.setHeaderText(null);
                alert.setContentText(message + "\nGame will start soon...");
                alert.show();
                
                // Tự động đóng sau 3 giây
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                        Platform.runLater(() -> {
                            if (alert.isShowing()) {
                                alert.close();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                // Nếu bị từ chối, hiển thị cảnh báo
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invitation Declined");
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            }
        });
    }
    
   // Phương thức đóng waiting dialog
    public void closeWaitingDialog() {
        Platform.runLater(() -> {
            if (currentWaitingDialog != null && currentWaitingDialog.isShowing()) {
                currentWaitingDialog.close();
                currentWaitingDialog = null;
                currentInviteTarget = null; // Reset target
            }
        });
    }
    
    // Phương thức đóng tất cả dialog
    private void closeCurrentDialogs() {
        Platform.runLater(() -> {
            if (currentWaitingDialog != null && currentWaitingDialog.isShowing()) {
                currentWaitingDialog.close();
                currentWaitingDialog = null;
            }
            if (currentInviteDialog != null && currentInviteDialog.isShowing()) {
                currentInviteDialog.close();
                currentInviteDialog = null;
            }
        });
    }

    // Các phương thức utility
    public void clearMessages() {
        Platform.runLater(() -> {
            messageArea.clear();
        });
    }
    
    // Phương thức được gọi khi rời khỏi Home (để đóng tất cả dialog)
    public void onLeaveHome() {
        closeCurrentDialogs();
    }
    public void showFinalMatchResult(MatchSummary matchSummary) {
        if (matchSummary == null) return;

        Platform.runLater(() -> {
            try {
                int currentUserId = currentUser.getId();
                int myWins = matchSummary.getMyWins(currentUserId);
                int opponentWins = matchSummary.getOpponentWins(currentUserId);
                int myTotalScore = matchSummary.getMyTotalScore(currentUserId);
                int opponentTotalScore = matchSummary.getOpponentTotalScore(currentUserId);

                // 🎯 Xác định kết quả chung cuộc
                String resultText;
                String resultEmoji;
                String color;

                if (myWins > opponentWins) {
                    resultText = "🏆 VICTORY!";
                    resultEmoji = "🎉";
                    color = "green";
                } else if (myWins < opponentWins) {
                    resultText = "💔 DEFEAT";
                    resultEmoji = "😢";
                    color = "red";
                } else {
                    if (myTotalScore > opponentTotalScore) {
                        resultText = "🏆 VICTORY!";
                        resultEmoji = "🎉";
                        color = "green";
                    } else if (myTotalScore < opponentTotalScore) {
                        resultText = "💔 DEFEAT";
                        resultEmoji = "😢";
                        color = "red";
                    } else {
                        resultText = "🤝 DRAW";
                        resultEmoji = "🤝";
                        color = "orange";
                    }
                }

                // 📋 Tạo dialog
                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("Match Finished!");
                dialog.setHeaderText(null);
                dialog.initStyle(StageStyle.UNDECORATED); // bỏ khung viền nếu muốn gọn

                // 🧱 Layout
                VBox content = new VBox(10);
                content.setAlignment(Pos.CENTER);
                content.setPadding(new Insets(20));
                content.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-background-radius: 10;");

                Label emojiLabel = new Label(resultEmoji);
                emojiLabel.setStyle("-fx-font-size: 36px;");

                Label titleLabel = new Label(resultText);
                titleLabel.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: 20px; -fx-font-weight: bold;", color));

                Label scoreLabel = new Label(String.format("Wins: %d - %d", myWins, opponentWins));
                scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

                Label totalScoreLabel = new Label(String.format("Total Points: (%d) - (%d)", myTotalScore, opponentTotalScore));
                totalScoreLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 13px;");

                content.getChildren().addAll(emojiLabel, titleLabel, scoreLabel, totalScoreLabel);

                // 🧩 Gán vào dialog
                dialog.getDialogPane().setContent(content);
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                Button closeButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
                closeButton.setVisible(false);

                dialog.show();

                // ⏳ Auto fade out + close
                PauseTransition pause = new PauseTransition(Duration.seconds(10));
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), dialog.getDialogPane().getScene().getRoot());
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                pause.setOnFinished(e -> fadeOut.play());
                fadeOut.setOnFinished(e -> dialog.close());
                pause.play();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}