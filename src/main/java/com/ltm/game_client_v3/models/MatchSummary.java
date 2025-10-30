package com.ltm.game_client_v3.models;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class MatchSummary {
   
    private int matchId;
    private int user1Id;        // Thêm userId
    private int user2Id;        // Thêm userId
    private int user1TotalScore;
    private int user2TotalScore;
    private int user1Score;
    private int user2Score;
    private int user1Wins;
    private int user2Wins;
    private int totalRounds;
    
    public MatchSummary() {}
    
    public MatchSummary(int matchId, int user1Id, int user2Id, int user1TotalScore, 
                       int user2TotalScore, int user1Wins, int user2Wins, int totalRounds) {
        this.matchId = matchId;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.user1TotalScore = user1TotalScore;
        this.user2TotalScore = user2TotalScore;
        this.user1Wins = user1Wins;
        this.user2Wins = user2Wins;
        this.totalRounds = totalRounds;
    }
    public MatchSummary(int matchId, int user1Id, int user2Id, int user1TotalScore, 
                       int user2TotalScore, int user1Score, int user2Score,
                       int user1Wins, int user2Wins, int totalRounds) {
        this.matchId = matchId;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.user1TotalScore = user1TotalScore;
        this.user2TotalScore = user2TotalScore;
        this.user1Score = user1Score;
        this.user2Score = user2Score;
        this.user1Wins = user1Wins;
        this.user2Wins = user2Wins;
        this.totalRounds = totalRounds;
    }
    
    // Getters and Setters
    public int getMatchId() { return matchId; }
    public void setMatchId(int matchId) { this.matchId = matchId; }
    
    public int getUser1Id() { return user1Id; }
    public void setUser1Id(int user1Id) { this.user1Id = user1Id; }
    
    public int getUser2Id() { return user2Id; }
    public void setUser2Id(int user2Id) { this.user2Id = user2Id; }
    
    public int getUser1TotalScore() { return user1TotalScore; }
    public void setUser1TotalScore(int user1TotalScore) { this.user1TotalScore = user1TotalScore; }
    
    public int getUser2TotalScore() { return user2TotalScore; }
    public void setUser2TotalScore(int user2TotalScore) { this.user2TotalScore = user2TotalScore; }
    
    public int getUser1Wins() { return user1Wins; }
    public void setUser1Wins(int user1Wins) { this.user1Wins = user1Wins; }
    
    public int getUser2Wins() { return user2Wins; }
    public void setUser2Wins(int user2Wins) { this.user2Wins = user2Wins; }
    
    public int getTotalRounds() { return totalRounds; }
    public void setTotalRounds(int totalRounds) { this.totalRounds = totalRounds; }
    
    // Parse từ JSONObject GAME_RESULT - ĐƠN GIẢN HƠN
    public static MatchSummary fromJson(JSONObject json) {
        try {
            int matchId = json.optInt("matchId");
            
            // Dùng optJSONObject để tránh exception
            JSONObject player1 = json.optJSONObject("player1");
            JSONObject player2 = json.optJSONObject("player2");
            
            if (player1 == null || player2 == null) {
                System.err.println("❌ Player data missing");
                return new MatchSummary();
            }
            
            int user1Id = player1.optInt("userId");
            int user2Id = player2.optInt("userId");
            int user1TotalScore = player1.optInt("totalScore");
            int user2TotalScore = player2.optInt("totalScore");
            int user1Wins = player1.optInt("totalWins");
            int user2Wins = player2.optInt("totalWins");
            int user1Score = player1.optInt("score");
            int user2Score = player2.optInt("score");

            JSONObject matchSummary = json.optJSONObject("matchSummary");
            int totalRounds = matchSummary != null ? matchSummary.optInt("totalRounds", 1) : 1;

            return new MatchSummary(matchId, user1Id, user2Id, user1TotalScore,
                    user2TotalScore, user1Score, user2Score, user1Wins, user2Wins, totalRounds);
                    
        } catch (Exception e) {
            System.err.println("❌ Error parsing MatchSummary");
            e.printStackTrace();
            return new MatchSummary();
        }
    }
    
    /**
     * Lấy thông tin của user hiện tại
     */
    public int getMyTotalScore(int currentUserId) {
        return (currentUserId == user1Id) ? user1TotalScore : user2TotalScore;
    }
    public int getMyScore(int currentUserId) {
        return (currentUserId == user1Id) ? user1Score : user2Score;
    }
    
    public int getMyWins(int currentUserId) {
        return (currentUserId == user1Id) ? user1Wins : user2Wins;
    }
    
    public int getOpponentTotalScore(int currentUserId) {
        return (currentUserId == user1Id) ? user2TotalScore : user1TotalScore;
    }
    
    public int getOpponentWins(int currentUserId) {
        return (currentUserId == user1Id) ? user2Wins : user1Wins;
    }
    
    @Override
    public String toString() {
        return String.format(
            "MatchSummary [matchId=%d, user1Id=%d, user2Id=%d, user1TotalScore=%d, user2TotalScore=%d, user1Wins=%d, user2Wins=%d, totalRounds=%d]",
            matchId, user1Id, user2Id, user1TotalScore, user2TotalScore, user1Wins, user2Wins, totalRounds
        );
    }

}
