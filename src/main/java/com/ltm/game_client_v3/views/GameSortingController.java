package com.ltm.game_client_v3.views;

import com.ltm.game_client_v3.controller.ClientManager;
import com.ltm.game_client_v3.controller.SoundManager;
import com.ltm.game_client_v3.models.GameData;
import com.ltm.game_client_v3.models.MatchSummary;
import com.ltm.game_client_v3.models.Question;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.geometry.Insets;

import java.net.URL;
import java.util.*;


import org.json.JSONObject;

import javafx.scene.Node;


public class GameSortingController implements Initializable {
    
    private ClientManager clientManager;
    private GameData gameData;
    private MatchSummary matchSummary;
    private int roundNumber = 1;
    
    // UI Components
    @FXML private Label roomIdLabel, timeLabel, orderLabel;
    @FXML private Label userNickname, userTotalMatches, userWins, userScore;
    @FXML private Label opponentNickname, opponentTotalMatches, opponentWins, opponentScore;
    @FXML private Label userCurrentScore, opponentCurrentScore;
    @FXML private Label userScoreChange, opponentScoreChange;
    @FXML private Pane gameZone;
    @FXML private VBox topRowContainer, bottomRowContainer;
    @FXML private Line dividerLine;
    @FXML private Button resetButton, sendButton, continueButton, exitButton;
    @FXML private ImageView userAvatar, opponentAvatar, backgroundImage;
    
    // Game state và dialog management
    private Timeline gameTimer;
    private int remainingTime = 0;
    private boolean gameInProgress = false;
    private boolean ascendingOrder = true;
    private List<NumberBox> topRowBoxes = new ArrayList<>();
    private List<NumberBox> bottomRowBoxes = new ArrayList<>();
    private Dialog<Void> currentCountdownDialog;
    private Dialog<Void> currentWaitingDialog;
    private Dialog<Void> currentResultWaitingDialog;

    
    // Constants
    private static final int BOX_WIDTH = 80;
    private static final int BOX_HEIGHT = 60;
    private static final int BOX_SPACING = 10;
    private static final int ROW_SPACING = 15;

    public void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }
    
    public void setGameData(GameData gameData) {
        this.gameData = gameData;
        
        populateGameData();
    }
    public void updateQuestion(Question question) {
        if (gameData != null) {
            gameData.setQuestion(question);
            roundNumber++;
            populateGameData();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupGameZone();
        setupButtonStyles();
        setupSound();
    }
    
    private void setupGameZone() {
        dividerLine.setStartX(0);
        dividerLine.setEndX(gameZone.getPrefWidth());
        dividerLine.setStartY(300);
        dividerLine.setEndY(300);
        dividerLine.getStrokeDashArray().addAll(5.0, 5.0);
        
        topRowContainer.setPrefWidth(gameZone.getPrefWidth());
        bottomRowContainer.setPrefWidth(gameZone.getPrefWidth());
        topRowContainer.setAlignment(Pos.TOP_CENTER);
        bottomRowContainer.setAlignment(Pos.TOP_CENTER);
    }
    
    private void setupButtonStyles() {
        setupButtonHoverEffects(resetButton);
        setupButtonHoverEffects(sendButton);
        setupButtonHoverEffects(continueButton);
        buttonGameNotStart();
    }
    
    private void setupButtonHoverEffects(Button button) {
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });
        
        button.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
    }
    private void setupSound() {
        SoundManager.stopMusic();;
    }
    
    private void populateGameData() {
        if (gameData == null) return;
        
        Platform.runLater(() -> {
            roomIdLabel.setText("ROOM ID: " + gameData.getMatchId());
            
            if (gameData.getSelf() != null) {
                userNickname.setText(safeText(gameData.getSelf().getNickname(), "Player"));
                userTotalMatches.setText("Matches: " + gameData.getSelf().getTotalMatches());
                System.out.println("Self Wins: " + gameData.getSelf().getTotalWins());
                userWins.setText("Wins: " + gameData.getSelf().getTotalWins());
                System.out.println("Self Score: " + gameData.getSelf().getTotalScore());
                userScore.setText("Score: " + gameData.getSelf().getTotalScore());
            }
            
            if (gameData.getOpponent() != null) {
                opponentNickname.setText(safeText(gameData.getOpponent().getNickname(), "Opponent"));
                opponentTotalMatches.setText("Matches: " + gameData.getOpponent().getTotalMatches());
                opponentWins.setText("Wins: " + gameData.getOpponent().getTotalWins());
                opponentScore.setText("Score: " + gameData.getOpponent().getTotalScore());
            }
           
            
            showCountdownAndStartGame();
        });
    }
    
    private void showCountdownAndStartGame() {
        if (gameData == null || gameData.getQuestion() == null) return;
        buttonGameStart();

        Platform.runLater(() -> {
            //closeCountdownDialog();

            Dialog<Void> countdownDialog = new Dialog<>();
            countdownDialog.getDialogPane().getStyleClass().add("countdown-dialog");
            countdownDialog.setTitle("Get Ready!");
            countdownDialog.setHeaderText(null);


            // Thêm nút để dialog hoạt động đúng
            countdownDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            // Ẩn nút Close khỏi giao diện
            Button closeButton = (Button) countdownDialog.getDialogPane().lookupButton(ButtonType.CLOSE);
            closeButton.setVisible(false);

            VBox content = new VBox(20);
            content.setAlignment(Pos.CENTER);
            content.setPadding(new Insets(20));

            Label instructionLabel = new Label(gameData.getQuestion().getInstruction());
            instructionLabel.getStyleClass().add("instruction-text");
            instructionLabel.setWrapText(true);
            instructionLabel.setMaxWidth(300);

            Label countdownLabel = new Label("5");
            countdownLabel.getStyleClass().add("countdown-text");

            content.getChildren().addAll(instructionLabel, countdownLabel);
            countdownDialog.getDialogPane().setContent(content);

            final int[] countdown = {5};
            Timeline countdownTimer = new Timeline();

            KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), e -> {
                if (countdown[0] > 1) {
                    countdown[0]--;
                    countdownLabel.setText(String.valueOf(countdown[0]));
                } else {
                    countdownTimer.stop();
                    Platform.runLater(() -> {
                        fadeOutAndClose(countdownDialog);
                        startGameImmediately();
                    });
                }
            });

            countdownTimer.getKeyFrames().add(keyFrame);
            countdownTimer.setCycleCount(5);

            currentCountdownDialog = countdownDialog;

            countdownDialog.setOnHidden(e -> {
                currentCountdownDialog = null;
                if (countdown[0] > 0) {
                    countdownTimer.stop();
                    startGameImmediately();
                }
            });

            countdownDialog.show();
            countdownTimer.play();

            // Auto close fallback
            PauseTransition autoClose = new PauseTransition(Duration.seconds(6));
            autoClose.setOnFinished(e -> fadeOutAndClose(countdownDialog));
            autoClose.play();
        });
    }

    /**
     * ✅ Thêm animation đóng mượt
     */
    private void fadeOutAndClose(Dialog<?> dialog) {
        if (dialog == null || !dialog.isShowing()) return;

        Node root = dialog.getDialogPane().getScene().getRoot();
        FadeTransition fade = new FadeTransition(Duration.millis(300), root);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> dialog.close());
        fade.play();
    }

    // private void closeCountdownDialog() {
    //     if (currentCountdownDialog != null && currentCountdownDialog.isShowing()) {
    //         fadeOutAndClose(currentCountdownDialog);
    //         currentCountdownDialog = null;
    //     }
    // }

    
    private void startGameImmediately() {
       
        Platform.runLater(() -> {
            buttonGameStart();
            setupQuestion(gameData.getQuestion());
            startGameTimer(gameData.getQuestion().getTimeLimit());
        });
    }
    
    private void setupQuestion(Question question) {
        if (question == null) return;
        
        ascendingOrder = "ASCENDING".equalsIgnoreCase(question.getSortOrder());
        orderLabel.setText("Order: " + (ascendingOrder ? "ASCENDING" : "DESCENDING"));
        
        generateNumberBoxes(question.getItems());
    }
    
    private void generateNumberBoxes(List<String> items) {
        clearGameZone();
        
        if (items == null || items.isEmpty()) {
            items = Arrays.asList("7", "2", "1", "9", "5", "12");
        }
        
        HBox topRow = createNumberRow(items, true);
        HBox bottomRow = createNumberRow(new ArrayList<>(), false);
        
        topRowContainer.getChildren().add(topRow);
        bottomRowContainer.getChildren().add(bottomRow);
    }
    
    private HBox createNumberRow(List<String> items, boolean isTopRow) {
        HBox row = new HBox(BOX_SPACING);
        row.setAlignment(Pos.CENTER);
        
        for (String item : items) {
            NumberBox numberBox = new NumberBox(item, isTopRow);
            row.getChildren().add(numberBox);
            
            if (isTopRow) {
                topRowBoxes.add(numberBox);
            } else {
                bottomRowBoxes.add(numberBox);
            }
        }
        
        return row;
    }
    
    private void clearGameZone() {
        topRowContainer.getChildren().clear();
        bottomRowContainer.getChildren().clear();
        topRowBoxes.clear();
        bottomRowBoxes.clear();
    }
    
    private void startGameTimer(int seconds) {
        remainingTime = seconds;
        updateTimeLabel();
        
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        gameTimer = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                remainingTime--;
                updateTimeLabel();
                
                if (remainingTime <= 0) {
                    gameTimer.stop();
                    handleTimeUp();
                } else if (remainingTime <= 10) {
                    timeLabel.getStyleClass().add("time-warning");
                } else {
                    timeLabel.getStyleClass().removeAll("time-warning");
                }
            })
        );
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }
    
    private void updateTimeLabel() {
        timeLabel.setText("Time: " + remainingTime + "s");
    }
    
    private void handleTimeUp() {
        showAlert("Time's up!", "Time's up! You lost.", Alert.AlertType.INFORMATION);
        sendUserAnswer("TIMEOUT");
    }
    
    @FXML
    private void onReset() {
        if (gameData != null && gameData.getQuestion() != null) {
            generateNumberBoxes(gameData.getQuestion().getItems());
        }
    }
    
    @FXML
    private void onSendAnswer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        boolean correct = isSortedCorrectly();
        if (correct) {
            showAlert("Exactly!", 
                "Congratulations! You won!\nTime: " + (gameData.getQuestion().getTimeLimit() - remainingTime) + "s", 
                Alert.AlertType.INFORMATION);
            sendUserAnswer("CORRECT");
        } else {
            showAlert("WRONG ANSWER", 
                "Not sorted correctly. Poor you!", 
                Alert.AlertType.WARNING);
            sendUserAnswer("WRONG");
        }
        continueButton.setDisable(true);
    }
    
    private boolean isSortedCorrectly() {
        List<String> playerOrder = bottomRowBoxes.stream()
            .map(box -> box.getValue())
            .toList();
            
        return playerOrder.equals(gameData.getQuestion().getCorrectAnswer());
    }
    
    @FXML
    private void onContinue() {
        if (gameData != null && gameData.getOpponent() != null) {
            invitePlayer(gameData.getOpponent().getUsername(), gameData.getOpponent().getNickname());
        }
    }
    private void invitePlayer(String username, String nickname) {
        
        showWaitingDialog("Waiting for " + nickname + " to respond...");

        JSONObject inviteMsg = new JSONObject();
        inviteMsg.put("action", "INVITE_USER_TO_NEXT_GAME");
        inviteMsg.put("targetUsername", username);
        clientManager.send(inviteMsg);
    }
    private void showWaitingDialog(String message) {
        Platform.runLater(() -> {
            // Đóng dialog cũ nếu có
            if (currentWaitingDialog != null && currentWaitingDialog.isShowing()) {
                currentWaitingDialog.close();
            }

            Dialog<Void> waitingDialog = new Dialog<>();
            waitingDialog.setTitle("Waiting for Response");
            waitingDialog.setHeaderText(message);

            ProgressIndicator progress = new ProgressIndicator();
            VBox content = new VBox(20, progress);
            content.setAlignment(Pos.CENTER);
            content.setPadding(new Insets(20));

            waitingDialog.getDialogPane().setContent(content);

            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            waitingDialog.getDialogPane().getButtonTypes().add(cancelButton);

            waitingDialog.setResultConverter(dialogButton -> {
                if (dialogButton == cancelButton) {
                    JSONObject cancelMsg = new JSONObject();
                    cancelMsg.put("action", "CANCEL_INVITE_TO_NEXT_GAME");
                    cancelMsg.put("inviterName", gameData.getSelf().getNickname());
                    cancelMsg.put("targetUsername", gameData.getOpponent().getUsername());
                    clientManager.send(cancelMsg);
                }
                return null;
            });

            currentWaitingDialog = waitingDialog; // ⚡ Lưu lại dialog hiện tại
            waitingDialog.show();
        });
    }
    public void closeWaitingDialog() {
        if (currentWaitingDialog != null && currentWaitingDialog.isShowing()) {
            fadeOutAndClose(currentWaitingDialog);
            currentWaitingDialog = null;
        }
    }

    public void showInviteDialog(String inviterUsername, String inviterNickname) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Continue Invitation");
            alert.setHeaderText("Game Continue Invitation Received!");
            alert.setContentText(inviterNickname + " invited you to play next game!\nDo you want to accept?");
            
            ButtonType acceptButton = new ButtonType("Accept");
            ButtonType rejectButton = new ButtonType("Reject");
            alert.getButtonTypes().setAll(acceptButton, rejectButton);
            
            // ❌ KHÔNG dùng showAndWait() vì nó block
            alert.show();

            // ✅ Tự đóng sau 30 giây nếu không nhấn gì
            PauseTransition autoClose = new PauseTransition(Duration.seconds(30));
            autoClose.setOnFinished(e -> {
                if (alert.isShowing()) {
                    alert.close();

                    // Nếu muốn mặc định gửi “Reject” khi hết thời gian:
                    JSONObject responseJson = new JSONObject();
                    responseJson.put("action", "INVITE_USER_TO_NEXT_GAME_RESPONSE");
                    responseJson.put("matchId", gameData.getMatchId());
                    responseJson.put("inviterUsername", inviterUsername);
                    responseJson.put("accepted", false);
                    clientManager.send(responseJson);
                }
            });
            autoClose.play();

            // ✅ Xử lý nếu người dùng nhấn nút
            alert.resultProperty().addListener((obs, oldResult, newResult) -> {
                if (newResult != null) {
                    boolean accepted = newResult == acceptButton;

                    JSONObject responseJson = new JSONObject();
                    responseJson.put("action", "INVITE_USER_TO_NEXT_GAME_RESPONSE");
                    responseJson.put("matchId", gameData.getMatchId());
                    responseJson.put("inviterUsername", inviterUsername);
                    responseJson.put("accepted", accepted);
                    clientManager.send(responseJson);

                    autoClose.stop(); // dừng auto-close nếu user đã bấm
                    alert.close();
                }
            });
        });
    }
    public void showInviteResult(String targetNickname, boolean accepted) {
        Platform.runLater(() -> {

            String message = targetNickname + (accepted ? " accepted your invitation!" : " declined your invitation.");  
            // Nếu bị từ chối, hiển thị cảnh báo
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invitation Declined");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            
        });
    }


   
    
  @FXML
    private void onExit() {
        // ⚠️ Hiển thị hộp thoại xác nhận
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm leaving the match!!");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to leave the match?");

        ButtonType yesButton = new ButtonType("Leave", ButtonBar.ButtonData.OK_DONE);
        ButtonType noButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesButton, noButton);

        // Hiển thị và chờ người dùng chọn
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == yesButton) {
            // ✅ Người dùng xác nhận rời
            if (gameTimer != null) {
                gameTimer.stop();
            }
            showResultWaitingDialog();

            if (gameInProgress) {
                sendUserAnswer("QUIT");
            } else {
                try {
                    JSONObject data = new JSONObject();
                    data.put("matchId", gameData.getMatchId());
                    data.put("userId", gameData.getSelf().getId());
                    data.put("opponentId", gameData.getOpponent().getId());

                    org.json.JSONObject msg = new org.json.JSONObject();
                    msg.put("action", "QUIT_GAME");
                    msg.put("data", data);

                    clientManager.send(msg);
                    System.out.println("Sent USER_ANSWER: " + msg);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            //clientManager.getViewManager().showHome();
        }
        // ❌ Nếu người dùng chọn "Hủy" thì không làm gì cả
    }
     private void showResultWaitingDialog() {
        Platform.runLater(() -> {


            Dialog<Void> waitingDialog = new Dialog<>();
            waitingDialog.setTitle("Waiting for Final Result");
            waitingDialog.setHeaderText("Please wait while we calculate the final match results...");
            waitingDialog.getDialogPane().getStyleClass().add("waiting-dialog");

            // Tạo progress indicator (dấu xoay vòng)
            ProgressIndicator progressIndicator = new ProgressIndicator();
            progressIndicator.setPrefSize(50, 50);
                // Thêm nút để dialog hoạt động đúng
            waitingDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            // Ẩn nút Close khỏi giao diện
            Button closeButton = (Button) waitingDialog.getDialogPane().lookupButton(ButtonType.CLOSE);
            closeButton.setVisible(false);
            Label messageLabel = new Label("Processing match data...");
            messageLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");

            VBox content = new VBox(20, progressIndicator, messageLabel);
            content.setAlignment(Pos.CENTER);
            content.setPadding(new Insets(20));
            content.setStyle("-fx-background-color: white;");

            waitingDialog.getDialogPane().setContent(content);
            
            // KHÔNG cho phép đóng bằng nút X hoặc ESC
            waitingDialog.setResultConverter(buttonType -> null);
            waitingDialog.initStyle(StageStyle.UTILITY);
            waitingDialog.setResizable(false);

            currentResultWaitingDialog = waitingDialog;
            waitingDialog.show();
        });
    }
    public void closeResultWaitingDialog() {
        Platform.runLater(() -> {
            if (currentResultWaitingDialog != null && currentResultWaitingDialog.isShowing()) {
                fadeOutAndClose(currentResultWaitingDialog);
                currentResultWaitingDialog = null;
            }
        });
    }

    
    private void sendUserAnswer(String status) {
        buttonGameNotStart();
       try {
            JSONObject data = new JSONObject();
            data.put("matchId", gameData.getMatchId());
            data.put("userId", gameData.getSelf().getId());
            data.put("roundNumber", roundNumber);
            data.put("timeCompleted", gameData.getQuestion().getTimeLimit() - remainingTime); // Thời gian tính bằng giây
            data.put("status", status);

            org.json.JSONObject msg = new org.json.JSONObject();
            msg.put("action", "SUBMIT_USER_ANSWER");
            msg.put("data", data);

            clientManager.send(msg);
            System.out.println("Sent USER_ANSWER: " + msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
    
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    private void showMatchResultAlert() {
        if (matchSummary == null || gameData == null) return;
        continueButton.setDisable(false);
        Platform.runLater(() -> {
            try {
                int currentUserId = gameData.getSelf().getId();
                int myWins = matchSummary.getMyWins(currentUserId);
                int opponentWins = matchSummary.getOpponentWins(currentUserId);
                int myTotalScore = matchSummary.getMyTotalScore(currentUserId);
                int opponentTotalScore = matchSummary.getOpponentTotalScore(currentUserId);
                
                // Xác định kết quả
                String resultEmoji;
                String resultColor;
                
                if (myWins > opponentWins) {
                    resultEmoji = "🏆";
                    resultColor = "GREEN";
                } else if (myWins < opponentWins) {
                    resultEmoji = "💔";
                    resultColor = "RED";
                } else {
                    resultEmoji = "🤝";
                    resultColor = "ORANGE";
                }
                
                // Tạo dialog custom đơn giản
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("Round Result");
                dialog.setHeaderText(resultEmoji + " Round " + roundNumber + " Finished!");
                
            
                // Nội dung
                VBox content = new VBox(10);
                content.setPadding(new Insets(10));
                content.setAlignment(Pos.CENTER);
                
                Label resultLabel = new Label();
                resultLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                
                if (myWins > opponentWins) {
                    resultLabel.setText("🎉 VICTORY! 🎉");
                    resultLabel.setStyle("-fx-text-fill: green; -fx-font-size: 18px; -fx-font-weight: bold;");
                } else if (myWins < opponentWins) {
                    resultLabel.setText("💔 DEFEAT");
                    resultLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px; -fx-font-weight: bold;");
                } else {
                    resultLabel.setText("🤝 DRAW");
                    resultLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 18px; -fx-font-weight: bold;");
                }
                
                Label scoreLabel = new Label(String.format("You get score: %d",matchSummary.getMyScore(currentUserId)));
                scoreLabel.setStyle("-fx-font-size: 14px;");
            
                
                 // Thêm nút để dialog hoạt động đúng
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

                // Ẩn nút Close khỏi giao diện
                Button closeButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CLOSE);

                closeButton.setVisible(false);
                content.getChildren().addAll(resultLabel, scoreLabel);
                dialog.getDialogPane().setContent(content);
                
                dialog.show();
                    // Auto close fallback
                PauseTransition autoClose = new PauseTransition(Duration.seconds(5));
                autoClose.setOnFinished(e -> fadeOutAndClose(dialog));
                autoClose.play();                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    private String safeText(String text, String defaultText) {
        return (text == null || text.isEmpty()) ? defaultText : text;
    }
    
    // Inner class for number boxes
    private class NumberBox extends StackPane {
        private String value;
        private boolean isTopRow;
        private Label numberLabel;
        
        public NumberBox(String value, boolean isTopRow) {
            this.value = value;
            this.isTopRow = isTopRow;
            
            setupAppearance();
            setupInteractions();
        }
        
        private void setupAppearance() {
            setPrefSize(BOX_WIDTH, BOX_HEIGHT);
            getStyleClass().add(isTopRow ? "number-box-top" : "number-box-bottom");
            
            numberLabel = new Label(value);
            numberLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            numberLabel.setTextFill(Color.BLACK);
            
            getChildren().add(numberLabel);
        }
        
        private void setupInteractions() {
            setOnMouseClicked(e -> handleBoxClick());
            
            setOnMouseEntered(e -> {
                setScaleX(1.1);
                setScaleY(1.1);
            });
            
            setOnMouseExited(e -> {
                setScaleX(1.0);
                setScaleY(1.0);
            });
        }
        
        private void handleBoxClick() {
            if (isTopRow) {
                moveToBottom();
            } else {
                moveToTop();
            }
        }
        
        private void moveToBottom() {
            topRowBoxes.remove(this);
            bottomRowBoxes.add(this);
            isTopRow = false;
            getStyleClass().clear();
            getStyleClass().add("number-box-bottom");
            updateVisualPosition();
        }
        
        private void moveToTop() {
            bottomRowBoxes.remove(this);
            topRowBoxes.add(this);
            isTopRow = true;
            getStyleClass().clear();
            getStyleClass().add("number-box-top");
            updateVisualPosition();
        }
        
        private void updateVisualPosition() {
            if (getParent() instanceof HBox) {
                ((HBox) getParent()).getChildren().remove(this);
            }
            
            HBox targetRow = isTopRow ? (HBox) topRowContainer.getChildren().get(0) 
                                     : (HBox) bottomRowContainer.getChildren().get(0);
            targetRow.getChildren().add(this);
        }
        
        public String getValue() {
            return value;
        }
    }
    
    public void updateMatchSummary(MatchSummary summary) {
        this.matchSummary = summary;
        updateMatchSummaryUI();
        showMatchResultAlert(); // Thêm dòng này
    }
    
    private void updateMatchSummaryUI() {
        if (matchSummary == null || gameData == null) return;
        
        Platform.runLater(() -> {
            try {
                int currentUserId = (gameData.getSelf().getId());
                
                int myWins = matchSummary.getMyWins(currentUserId);
                int opponentWins = matchSummary.getOpponentWins(currentUserId);
                int myTotalScore = matchSummary.getMyTotalScore(currentUserId);
                int opponentTotalScore = matchSummary.getOpponentTotalScore(currentUserId);
                
                userCurrentScore.setText(String.valueOf(myWins));
                opponentCurrentScore.setText(String.valueOf(opponentWins));
                
                updateScoreLabel(userScoreChange, myTotalScore);
                updateScoreLabel(opponentScoreChange, opponentTotalScore);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    private void updateScoreLabel(Label label, int score) {
        String text = (score >= 0 ? "+" : "") + score;
        label.setText(text);
        
        if (score >= 0) {
            label.getStyleClass().removeAll("score-negative");
            label.getStyleClass().add("score-positive");
        } else {
            label.getStyleClass().removeAll("score-positive");
            label.getStyleClass().add("score-negative");
        }
    }
    
    public void increaseRoundNumber() {
        this.roundNumber++;
    }
    
    // Phương thức cleanup khi rời khỏi game
    public void onLeaveGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
       // closeCountdownDialog();
    }
    private void buttonGameStart() {
        gameInProgress = true;
        resetButton.setDisable(false);
        sendButton.setDisable(false);
        continueButton.setDisable(true);
    }
    private void buttonGameNotStart() {
        gameInProgress = false;
        resetButton.setDisable(true);
        sendButton.setDisable(true);
        continueButton.setDisable(false);
    }
        /**
     * Đóng tất cả dialog đang mở
     */
    public void closeAllDialogs() {
        Platform.runLater(() -> {
            // Đóng dialog chờ kết quả
            if (currentResultWaitingDialog != null && currentResultWaitingDialog.isShowing()) {
                currentResultWaitingDialog.close();
                currentResultWaitingDialog = null;
            }
            
            // Đóng dialog chờ tiếp tục
            if (currentWaitingDialog != null && currentWaitingDialog.isShowing()) {
                currentWaitingDialog.close();
                currentWaitingDialog = null;
            }
            
            // Đóng dialog đếm ngược
            if (currentCountdownDialog != null && currentCountdownDialog.isShowing()) {
                currentCountdownDialog.close();
                currentCountdownDialog = null;
            }
        });
    }
}