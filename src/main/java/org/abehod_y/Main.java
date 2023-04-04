package org.abehod_y;

import org.abehod_y.picovoice.CheetahRunner;
import org.abehod_y.picovoice.CheetahRunner;
import org.abehod_y.picovoice.PorcupineRunner;
import org.abehod_y.spotify.SpotifyPlayer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) throws IOException {
        InputStream input = new FileInputStream("src/main/resources/config.properties");
        Properties prop = new Properties();
        prop.load(input);

        String clientId = prop.getProperty("clientId");
        String clientSecret = prop.getProperty("clientSecret");
        String deviceId = prop.getProperty("deviceId");
        String accessToken = prop.getProperty("accessTokenSpotify");
        String refreshToken = prop.getProperty("refreshToken");

        SpotifyPlayer spotifyPlayer = new SpotifyPlayer(clientId, clientSecret,
                deviceId, accessToken, refreshToken);

        String accessKey = prop.getProperty("accessKey");
        String keywordPath = prop.getProperty("keywordPath");
        String contextPath = prop.getProperty("contextPath");

        try {
            PorcupineRunner porcupine = new PorcupineRunner(accessKey, keywordPath, contextPath, spotifyPlayer);
            porcupine.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
