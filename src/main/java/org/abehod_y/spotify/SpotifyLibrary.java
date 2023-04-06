package org.abehod_y.spotify;

import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.library.GetUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.library.RemoveUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.library.SaveTracksForUserRequest;

import java.io.IOException;

public class SpotifyLibrary extends SpotifyBuilder {

    SpotifyLibrary(String clientId, String clientSecret, String deviceId,
             String refreshToken) {
        super(clientId, clientSecret, deviceId, refreshToken);
    }

    SavedTrack[] getUserSavedTracks() throws IOException, ParseException, SpotifyWebApiException {
        GetUsersSavedTracksRequest getUsersSavedTracksRequest = this.getSpotifyApi().getUsersSavedTracks()
                .limit(50)
                .offset(0)
                .build();

        return getUsersSavedTracksRequest.execute().getItems();
    }

    void saveTrackToLiked(String trackId) throws IOException, ParseException, SpotifyWebApiException {
        SaveTracksForUserRequest saveTracksForUserRequest = this.getSpotifyApi()
                .saveTracksForUser(trackId)
                .build();
        saveTracksForUserRequest.execute();
    }

    void removeTrackFromLiked(String trackId) throws IOException, ParseException, SpotifyWebApiException {
        RemoveUsersSavedTracksRequest removeUsersSavedTracksRequest = this.getSpotifyApi()
                .removeUsersSavedTracks(trackId)
                .build();
        removeUsersSavedTracksRequest.execute();
    }

}
