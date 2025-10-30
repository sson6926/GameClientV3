package com.ltm.game_client_v3.models;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Question {
    private String type;        // "NUMBERS" hoặc "LETTERS"
    private String sortOrder;   // "ASCENDING" hoặc "DESCENDING"
    private int timeLimit;      // 15, 30, 45...
    private String instruction; // hướng dẫn
    private List<String> items; // danh sách item
    private final List<String> correctAnswer;

    public Question(String type, String sortOrder, int timeLimit, String instruction, List<String> items, List<String> correctAnswer) {
        this.type = type;
        this.sortOrder = sortOrder;
        this.timeLimit = timeLimit;
        this.instruction = instruction;
        this.items = items;
        this.correctAnswer = correctAnswer;
    }

    // Getters
    public String getType() { return type; }
    public String getSortOrder() { return sortOrder; }
    public int getTimeLimit() { return timeLimit; }
    public String getInstruction() { return instruction; }
    public List<String> getItems() { return items; }
    public List<String> getCorrectAnswer() { return correctAnswer; }

    // Parse từ JSONObject
    public static Question fromJson(JSONObject json) {
        String type = json.optString("typeQues");
        String sortOrder = json.optString("sortOrder");
        int timeLimit = json.optInt("timeLimit");
        String instruction = json.optString("instruction");


        List<String> items = new ArrayList<>();
        JSONArray itemsArray = json.optJSONArray("items");
        if (itemsArray != null) {
            for (int i = 0; i < itemsArray.length(); i++) {
                items.add(itemsArray.optString(i));
            }
        }
        List<String> correctAnswer = new ArrayList<>();
        JSONArray answerArray = json.optJSONArray("correctAnswer");
        if (answerArray != null) {
            for (int i = 0; i < answerArray.length(); i++) {
                correctAnswer.add(answerArray.optString(i));
            }
        }

        return new Question(type, sortOrder, timeLimit, instruction, items, correctAnswer);
    }
}