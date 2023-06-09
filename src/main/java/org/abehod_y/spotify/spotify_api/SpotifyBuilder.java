package org.abehod_y.spotify.spotify_api;

import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class SpotifyBuilder {
    private final SpotifyApi spotifyApi;

    protected SpotifyBuilder(String clientId, String clientSecret, String refreshToken) {
        this.spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(refreshToken)
                .build();
        this.updateToken();
    }

    public SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }

    void updateToken() {
        Timer timer = new Timer();
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {
            try {
                AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();
                String newAccessToken = authorizationCodeRefreshRequest
                        .execute()
                        .getAccessToken();
                spotifyApi.setAccessToken(newAccessToken);
            } catch (IOException | ParseException | SpotifyWebApiException e) {
                System.out.println("Error: " + e.getMessage());
            }
            }
        };
        timer.schedule(hourlyTask, 0L, 1000*60*60);
    }
}
