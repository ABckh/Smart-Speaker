package org.abehod_y;

import ai.picovoice.cheetah.CheetahException;
import ai.picovoice.picovoice.PicovoiceException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException, PicovoiceException, CheetahException {
        InputStream input = new FileInputStream("src/main/resources/config.properties");
        Properties prop = new Properties();
        prop.load(input);

        String clientId = prop.getProperty("spotifyClientId");
        String clientSecret = prop.getProperty("spotifyClientSecret");
        String deviceId = prop.getProperty("spotifyDeviceId");
        String refreshToken = prop.getProperty("spotifyRefreshToken");

        String picovoiceAccessKey = prop.getProperty("picovoiceAccessKey");
        String porcupineKeywordPath = prop.getProperty("porcupineKeywordPath");
        String rhinoContextPath = prop.getProperty("rhinoContextPath");

        new SmartSpeaker.Builder()
                .setPicovoiceAccessKey(picovoiceAccessKey)
                .setPorcupineKeywordPath(porcupineKeywordPath)
                .setRhinoContextPath(rhinoContextPath)
                .setSpotifyClientId(clientId)
                .setSpotifyClientSecret(clientSecret)
                .setSpotifyDeviceId(deviceId)
                .setSpotifyRefreshToken(refreshToken)
                .build()
                .run();

        input.close();
    }
}
