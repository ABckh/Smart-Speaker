package org.abehod_y.spotify.spotify_api;

import se.michaelthelin.spotify.model_objects.specification.SavedTrack;
import se.michaelthelin.spotify.requests.data.library.GetUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.library.RemoveUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.library.SaveTracksForUserRequest;

import java.util.Objects;

import static org.abehod_y.spotify.spotify_api.helpers.Requests.*;

public class SpotifyLibrary extends SpotifyBuilder {

    SpotifyLibrary(String clientId, String clientSecret, String refreshToken) {
        super(clientId, clientSecret, refreshToken);
    }

    SavedTrack[] getUserSavedTracks() {
        GetUsersSavedTracksRequest getUsersSavedTracksRequest = this.getSpotifyApi().getUsersSavedTracks()
                .limit(50)
                .offset(0)
                .build();

        return Objects.requireNonNull(executeRequestWithReturn(getUsersSavedTracksRequest)).getItems();
    }

    public void saveTrackToLiked(String trackId) {
        SaveTracksForUserRequest saveTracksForUserRequest = this.getSpotifyApi()
                .saveTracksForUser(trackId)
                .build();

        executeRequest(saveTracksForUserRequest);
    }

    public void removeTrackFromLiked(String trackId) {
        RemoveUsersSavedTracksRequest removeUsersSavedTracksRequest = this.getSpotifyApi()
                .removeUsersSavedTracks(trackId)
                .build();

        executeRequest(removeUsersSavedTracksRequest);
    }
}
