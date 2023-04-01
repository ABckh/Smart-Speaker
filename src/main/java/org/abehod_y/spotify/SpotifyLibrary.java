package org.abehod_y.spotify;

import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.library.GetUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.library.RemoveUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.library.SaveTracksForUserRequest;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchPlaylistsRequest;

import java.io.IOException;

public class SpotifyLibrary extends SpotifyBuilder {

    SpotifyLibrary(String clientId, String clientSecret, String deviceId,
            String accessToken, String refreshToken) {
        super(clientId, clientSecret, deviceId, accessToken, refreshToken);
    }

    SavedTrack[] getUserSavedTracks() {
        GetUsersSavedTracksRequest getUsersSavedTracksRequest = this.getSpotifyApi().getUsersSavedTracks()
                .limit(50)
                .offset(0)
                .build();

        SavedTrack[] savedTracks = null;
        try {
            savedTracks = getUsersSavedTracksRequest.execute().getItems();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return savedTracks;
    }

    public void saveTrackToLiked() {
        String trackId = getUsersCurrentlyPlayingTrackId();
        SaveTracksForUserRequest saveTracksForUserRequest = this.getSpotifyApi()
                .saveTracksForUser(trackId)
                .build();
        try {
            saveTracksForUserRequest.execute();
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void removeTrackFromLiked() {
        String trackId = getUsersCurrentlyPlayingTrackId();
        RemoveUsersSavedTracksRequest removeUsersSavedTracksRequest = this.getSpotifyApi()
                .removeUsersSavedTracks(trackId)
                .build();
        try {
            removeUsersSavedTracksRequest.execute();
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    private String getUsersCurrentlyPlayingTrackId() {
        final GetUsersCurrentlyPlayingTrackRequest getUsersCurrentlyPlayingTrackRequest = this.getSpotifyApi()
                .getUsersCurrentlyPlayingTrack()
                .build();
        String trackId = null;
        try {
            final CurrentlyPlaying currentlyPlaying = getUsersCurrentlyPlayingTrackRequest.execute();
            if (currentlyPlaying.getIs_playing()) {
                trackId = currentlyPlaying.getItem().getId();
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return trackId;
    }

    String getArtistId(String artistName) {
        final SearchArtistsRequest searchArtistsRequest = this.getSpotifyApi()
                .searchArtists(artistName)
                .build();
        String artistId = null;
        try {
            final Paging<Artist> artistPaging = searchArtistsRequest.execute();
            Artist artist = artistPaging.getItems()[0];
            artistId = artist.getId();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return artistId;
    }

    String getReleaseRadarPlaylistId() {
        final SearchPlaylistsRequest searchPlaylistRequest = this.getSpotifyApi()
                .searchPlaylists("Release Radar")
                .build();

        String playlistId = null;
        try {
             PlaylistSimplified[] playlists = searchPlaylistRequest
                    .execute()
                    .getItems();

             playlistId = playlists[0].getId();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return playlistId;
    }
}
