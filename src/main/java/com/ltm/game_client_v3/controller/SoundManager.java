package com.ltm.game_client_v3.controller;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundManager {
    private static MediaPlayer bgPlayer;

    public static void playBackgroundMusic() {
        try {
            if (bgPlayer != null) {
                bgPlayer.stop();
            }

            String path = SoundManager.class.getResource("/sounds/bg-music.mp3").toExternalForm();
            Media media = new Media(path);
            bgPlayer = new MediaPlayer(media);

            bgPlayer.setVolume(0.6);
            bgPlayer.setCycleCount(MediaPlayer.INDEFINITE); // phát lặp
            bgPlayer.play();

            System.out.println("🎵 Background music started");
        } catch (Exception e) {
            System.err.println("⚠️ Error loading music: " + e.getMessage());
        }
    }

    public static void toggleMute() {
        if (bgPlayer != null) {
            boolean current = bgPlayer.isMute();
            bgPlayer.setMute(!current);
            System.out.println("🔈 Music muted: " + !current);
        }
    }

    public static boolean isMuted() {
        return bgPlayer != null && bgPlayer.isMute();
    }

    // 🛑 Dừng nhạc
    public static void stopMusic() {
        if (bgPlayer != null) {
            bgPlayer.stop();
            System.out.println("⏹ Music stopped");
        }
    }
}