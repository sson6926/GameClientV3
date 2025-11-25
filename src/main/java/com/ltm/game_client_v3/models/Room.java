package com.ltm.game_client_v3.models;

import org.json.JSONObject;

public class Room {
    private String roomCode;
    private User host;
    private int playerCount;
    private int maxPlayers;
    private String status;

    public Room(String roomCode, User host, int playerCount, int maxPlayers, String status) {
        this.roomCode = roomCode;
        this.host = host;
        this.playerCount = playerCount;
        this.maxPlayers = maxPlayers;
        this.status = status;
    }

    public static Room fromJson(JSONObject json) {
        String roomCode = json.optString("roomCode");
        JSONObject hostJson = json.optJSONObject("host");
        User host = hostJson != null ? User.fromJson(hostJson) : null;
        int playerCount = json.optInt("playerCount");
        int maxPlayers = json.optInt("maxPlayers");
        String status = json.optString("status");

        return new Room(roomCode, host, playerCount, maxPlayers, status);
    }

    // Getters
    public String getRoomCode() { return roomCode; }
    public User getHost() { return host; }
    public int getPlayerCount() { return playerCount; }
    public int getMaxPlayers() { return maxPlayers; }
    public String getStatus() { return status; }

    // Setters
    public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }
    public void setStatus(String status) { this.status = status; }
}