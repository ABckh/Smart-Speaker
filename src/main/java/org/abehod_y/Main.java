package org.abehod_y;

import org.abehod_y.picovoice.PorcupineWakeWord;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            PorcupineWakeWord.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        InputStream input = new FileInputStream("src/main/resources/config.properties");
//        Properties prop = new Properties();
//        prop.load(input);
//
//        String clientId = prop.getProperty("clientId");
//        String clientSecret = prop.getProperty("clientSecret");
//        String deviceId = prop.getProperty("deviceId");
//        String accessToken = prop.getProperty("accessToken");
//        String refreshToken = prop.getProperty("refreshToken");
//
//        SpotifyPlayer player = new SpotifyPlayer(clientId, clientSecret,
//                deviceId, accessToken, refreshToken);
//        player.resumePlaying();
//        player.playSomething();
//        player.searchTracks();
    }
}