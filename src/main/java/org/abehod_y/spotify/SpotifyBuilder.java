package org.abehod_y.spotify;

import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SpotifyBuilder {
    private final String deviceId;
    private final SpotifyApi spotifyApi;

    public SpotifyBuilder(String clientId, String clientSecret,
                          String deviceId, String accessToken, String refreshToken) {
        this.deviceId = deviceId;

        spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .build();

        this.updateToken();
    }

    public void updateToken() {
        try {
            AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();
            String newAccessToken = authorizationCodeRefreshRequest
                    .execute()
                    .getAccessToken();
            updateTokenInProperties(newAccessToken);
            spotifyApi.setAccessToken(newAccessToken);
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateTokenInProperties(String newAccessToken) {
        try {
            InputStream input = new FileInputStream("src/main/resources/config.properties");
            Properties prop = new Properties();
            prop.load(input);
            prop.setProperty("accessTokenSpotify", newAccessToken);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public String getDeviceId() {
        return deviceId;
    }

    public SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }
}
