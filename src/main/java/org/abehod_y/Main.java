package org.abehod_y;

import ai.picovoice.picovoice.PicovoiceException;
import org.abehod_y.helpers.SmartSpeaker;
import org.abehod_y.spotify.SpotifyPlayer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException, PicovoiceException {
        InputStream input = new FileInputStream("src/main/resources/config.properties");
        Properties prop = new Properties();
        prop.load(input);

        String clientId = prop.getProperty("spotifyClientId");
        String clientSecret = prop.getProperty("spotifyClientSecret");
        String deviceId = prop.getProperty("spotifyDeviceId");
        String refreshToken = prop.getProperty("spotifyRefreshToken");

        SpotifyPlayer spotifyPlayer = new SpotifyPlayer(clientId, clientSecret,
                deviceId, refreshToken);

        String picovoiceAccessKey = prop.getProperty("picovoiceAccessKey");
        String porcupineKeywordPath = prop.getProperty("porcupineKeywordPath");
        String rhinoContextPath = prop.getProperty("rhinoContextPath");

        new SmartSpeaker.Builder()
                .setPicovoiceAccessKey(picovoiceAccessKey)
                .setPorcupineKeywordPath(porcupineKeywordPath)
                .setRhinoContextPath(rhinoContextPath)
                .setSpotifyPlayer(spotifyPlayer)
                .build()
                .run();

        input.close();
    }
}
