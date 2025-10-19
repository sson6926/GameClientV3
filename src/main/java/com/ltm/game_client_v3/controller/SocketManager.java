package com.ltm.game_client_v3.controller;

import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class SocketManager implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private final Consumer<JSONObject> onMessage;
    private volatile boolean running = true;

    public SocketManager(String host, int port, Consumer<JSONObject> onMessage) {
        this.onMessage = onMessage;
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException("Connection failed: " + e.getMessage(), e);
        }
    }

    public void send(JSONObject data) {
        System.out.println("Sending: " + data.toString());
        try {
            out.write(data.toString());
            out.newLine();
            out.flush();
        } catch (IOException e) {
            System.err.println("Send failed: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String line;
            while (running && (line = in.readLine()) != null) {
                JSONObject msg = new JSONObject(line);
                onMessage.accept(msg);
            }
        } catch (IOException e) {
            System.err.println("Lost connection: " + e.getMessage());
        } finally {
            try { if (socket != null && !socket.isClosed()) socket.close(); } catch (IOException ignored) {}
        }
    }

    public void stop() {
        running = false;
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }
}