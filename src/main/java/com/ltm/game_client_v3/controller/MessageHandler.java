package com.ltm.game_client_v3.controller;

import com.ltm.game_client_v3.models.GameData;
import com.ltm.game_client_v3.models.MatchSummary;
import com.ltm.game_client_v3.models.Question;
import com.ltm.game_client_v3.models.User;
import com.ltm.game_client_v3.views.AuthController;
import com.ltm.game_client_v3.views.GameSortingController;
import com.ltm.game_client_v3.views.HomeController;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;

public class MessageHandler {
    private final ClientManager clientManager;
    private final UserManager userManager;
    private final ViewManager viewManager;

    public MessageHandler(ClientManager clientManager, UserManager userManager, ViewManager viewManager) {
        this.clientManager = clientManager;
        this.userManager = userManager;
        this.viewManager = viewManager;
    }

    public void handleMessage(JSONObject msg) {
        System.out.println("Received message: " + msg.toString());
        String action = msg.optString("action");
        switch (action) {
            case "LOGIN_RESPONSE" -> {
                String status = msg.optString("status");
                if ("success".equals(status)) {
                    JSONObject userData = msg.optJSONObject("user");
                    System.out.println("Login successful for user: " +  userData.optString("nickname"));
//                    userManager.setCurrentUser(new User(userData.optString("nickname")));
                    // Lấy đầy đủ thông tin user từ server
                    int id = userData.optInt("id");
                    String username = userData.optString("username");
                    String nickname = userData.optString("nickname");
                    int totalMatches = userData.optInt("total_matches");
                    int totalWins = userData.optInt("total_wins");
                    int totalScore = userData.optInt("total_score");

                    // Tạo user với đầy đủ thông tin
                    User user = new User(id, username, nickname, totalMatches, totalWins, totalScore);
                    userManager.setCurrentUser(user);
//                    clientManager.setUserManager(userManager);
                    //viewManager.showGamePlay();
                    viewManager.showWelcome();
                } else {
                    AuthController authController = viewManager.getAuthController();
                    authController.showLoginError(msg.optString("message"));
                }
            }
            case "REGISTER_RESPONSE" -> {
                String status = msg.optString("status");
                AuthController authController = viewManager.getAuthController();
                if ("success".equals(status)) {
                    if (authController != null)
                        authController.showNotify(msg.optString("message"));
                        authController.showLogin();
                } else {
                    if (authController != null)
                        authController.showRegisterError(msg.optString("message"));
                }
//
            }
            case "GET_ONLINE_USERS_RESPONSE" -> {
                JSONArray onlineUsers = msg.getJSONArray("onlineUsers");
                System.err.println("Online users received: " + onlineUsers.toString());
                HomeController homeController = viewManager.getHomeController();
                if (homeController != null) {
                    List<User> usersList = new ArrayList<>();
                    for (int i = 0; i < onlineUsers.length(); i++) {
                        org.json.JSONObject userObj = onlineUsers.optJSONObject(i);
                        if (userObj != null) {
                            int id = userObj.optInt("id");
                            String username = userObj.optString("username");
                            String nickname = userObj.optString("nickname");
                            boolean isOnline = userObj.optBoolean("isOnline");
                            boolean isPlaying = userObj.optBoolean("isPlaying");
                            
                            if (!username.equals(this.userManager.getCurrentUser().getUsername())) {
                                User user = new User(id,username, nickname, isOnline, isPlaying);
                                usersList.add(user);
                            }
                            
                        }
                    }

                    homeController.updateOnlinePlayersList(usersList);
                }
            }
            case "INVITE_USER_TO_GAME_REQUEST" -> {
                HomeController homeController = viewManager.getHomeController();
                if (homeController != null) {
                    homeController.showInviteDialog(msg.optString("inviterUsername"), msg.optString("inviterNickname"));
                }
            }
             case "INVITE_USER_TO_GAME_RESULT" -> {
                HomeController homeController = viewManager.getHomeController();
                if (homeController != null) {
                    // Hiển thị kết quả mời (chấp nhận/từ chối)
                    homeController.showInviteResult(msg.optString("opponentNickname"), msg.optBoolean("accepted"));
                    
                    // Nếu bị từ chối, không làm gì thêm
                    // Nếu được chấp nhận, chờ message START_GAME từ server
                }
            }
            case "START_GAME" -> {
                System.out.println("Starting game with data: " + msg);
                
                HomeController homeController = viewManager.getHomeController();
                if (homeController != null) {
                    // Đóng tất cả dialog ở Home trước khi chuyển sang game
                    homeController.onLeaveHome();
                    
                    // Hiển thị thông báo game sắp bắt đầu
                    homeController.addMessage("Game is starting...");
                }

                try {
                    GameData gameData = GameData.fromJson(msg);
                    
                    // In ra debug
                    System.out.println("Match ID: " + gameData.getMatchId());
                    System.out.println("Self: " + gameData.getSelf().getUsername());
                    System.out.println("Opponent: " + gameData.getOpponent().getUsername());
                    if (gameData.getQuestion() != null) {
                        System.out.println("Question: " + gameData.getQuestion().getInstruction());
                        System.out.println("Items: " + gameData.getQuestion().getItems());
                        System.out.println("Correct Answers: " + gameData.getQuestion().getCorrectAnswer());
                    }

                    // Truyền vào view
                    viewManager.showGamePlay(gameData);
                    
                } catch (Exception e) {
                    System.err.println("Error parsing game data: " + e.getMessage());
                    e.printStackTrace();
                }          
                          
            }
            case "GAME_RESULT" -> {
                System.out.println("Received GAME_RESULT: " + msg);
                MatchSummary summary = MatchSummary.fromJson(msg);
                GameSortingController gameSortingController = viewManager.getGameSortingController();
                if (gameSortingController != null) {
                    gameSortingController.updateMatchSummary(summary);
                } else {
                    // Fallback: log chi tiết nếu không có view
                    try {
                        int matchId = msg.getInt("matchId");
                        JSONObject player1 = msg.getJSONObject("player1");
                        JSONObject player2 = msg.getJSONObject("player2");

                        System.out.println("Match ID: " + matchId);
                        System.out.println("Player1: " + player1);
                        System.out.println("Player2: " + player2);
                    } catch (Exception e) {
                        System.err.println("Error parsing GAME_RESULT: " + e.getMessage());
                    }
                }
            }
             case "INVITE_NEXT_GAME_REQUEST" -> {
                System.out.println("Received INVITE_NEXT_GAME_REQUEST: " + msg);
                GameSortingController gameSortingController = viewManager.getGameSortingController();
                if (gameSortingController != null) {
                    gameSortingController.showInviteDialog(msg.optString("inviterUsername"), msg.optString("inviterNickname"));
                }
               
            }
            case "CONTINUE_NEXT_GAME_REJECTED" -> {
                GameSortingController gameSortingController = viewManager.getGameSortingController();
                if (gameSortingController != null) {
                    gameSortingController.showInviteResult(msg.optString("opponentNickname"), msg.optBoolean("accepted"));
                }
            }
            case "CONTINUE_NEXT_GAME" -> {
                System.out.println("Received CONTINUE_NEXT_GAME: " + msg);
                GameSortingController gameSortingController = viewManager.getGameSortingController();
                if (gameSortingController != null) {
                    // Cập nhật câu hỏi mới
                    Question newQuestion = Question.fromJson(msg.optJSONObject("question"));
                    gameSortingController.closeWaitingDialog();
                    gameSortingController.updateQuestion(newQuestion);
                }
            }
            case "GAME_FINAL_RESULT" -> {
                System.out.println("Received GAME_FINAL_RESULT: " + msg);
                MatchSummary summary = MatchSummary.fromJson(msg);
                  // Đóng dialog chờ kết quả nếu có
                GameSortingController gameSortingController = viewManager.getGameSortingController();
                if (gameSortingController != null) {
                    gameSortingController.closeResultWaitingDialog();
                }
                viewManager.showMatchResult(summary);
                
            }

            case "GET_RANKING_RESPONSE" -> {
                JSONArray rankingArray = msg.getJSONArray("ranking");
                HomeController homeController = viewManager.getHomeController();
                Platform.runLater(() -> homeController.openScoreboardPopup(rankingArray));
            }


            
            default -> System.out.println("Unknown message type: " + action);
        }
    }
}