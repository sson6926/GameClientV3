package com.ltm.game_client_v3.models;

import java.time.LocalDateTime;

public class MatchHistory {
    private int matchId;
    private String opponentUsername;
    private String opponentNickname;
    private String result;  // "WIN", "LOSS", "DRAW"
    private int userTotalScore;
    private int opponentTotalScore;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int roundCount;

    public MatchHistory(int matchId, String opponentUsername, String opponentNickname,
                        String result, int userTotalScore, int opponentTotalScore,
                        LocalDateTime startTime, LocalDateTime endTime, int roundCount) {
        this.matchId = matchId;
        this.opponentUsername = opponentUsername;
        this.opponentNickname = opponentNickname;
        this.result = result;
        this.userTotalScore = userTotalScore;
        this.opponentTotalScore = opponentTotalScore;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roundCount = roundCount;
    }

    // Getters (theo pattern của User.java)
    public int getMatchId() { return matchId; }
    public String getOpponentUsername() { return opponentUsername; }
    public String getOpponentNickname() { return opponentNickname; }
    public String getResult() { return result; }
    public int getUserTotalScore() { return userTotalScore; }
    public int getOpponentTotalScore() { return opponentTotalScore; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public int getRoundCount() { return roundCount; }

    @Override
    public String toString() {
        return String.format(
                "MatchHistory [matchId=%d, opponent=%s, result=%s, score=%d-%d, rounds=%d]",
                matchId, opponentNickname, result, userTotalScore, opponentTotalScore, roundCount
        );
    }
}
