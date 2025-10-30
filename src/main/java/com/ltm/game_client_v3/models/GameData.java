package com.ltm.game_client_v3.models;

import org.json.JSONObject;

public class GameData {
    private int matchId;
    private User self;
    private User opponent;
    private Question question;

    public GameData(int matchId, User self, User opponent, Question question) {
        this.matchId = matchId;
        this.self = self;
        this.opponent = opponent;
        this.question = question;
    }

    public static GameData fromJson(JSONObject msg) {
        int matchId = msg.optInt("matchId");
        User self = User.fromJson(msg.optJSONObject("self"));
        User opponent = User.fromJson(msg.optJSONObject("opponent"));
        Question question = Question.fromJson(msg.optJSONObject("question"));
        return new GameData(matchId, self, opponent, question);
    }

    public int getMatchId() {
        return matchId;
    }
    public User getSelf() {
        return self;
    }
    public User getOpponent() {
        return opponent;
    }
    public Question getQuestion() {
        return question;
    }
    public void setQuestion(Question question) {
        this.question = question;
    }

}
