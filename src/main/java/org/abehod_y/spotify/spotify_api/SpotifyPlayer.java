package org.abehod_y.spotify.spotify_api;

import com.google.gson.JsonParser;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.player.*;

import static org.abehod_y.spotify.spotify_api.helpers.ArraysHelpers.*;
import static org.abehod_y.spotify.spotify_api.helpers.SpotifyItemsIds.*;
import static org.abehod_y.spotify.spotify_api.helpers.Requests.*;


public class SpotifyPlayer extends SpotifyLibrary {
    private final String deviceId;

    public SpotifyPlayer(String clientId, String clientSecret, String refreshToken, String deviceId) {
        super(clientId, clientSecret, refreshToken);
        this.deviceId = deviceId;
    }

    public void playRandomMusic() {
        SavedTrack[] savedTracks = getUserSavedTracks();
        shuffle(savedTracks);
        playMultipleTracksInARow(savedTracks);
    }

    public void playRandomAlbum() {
        SavedAlbum[] savedAlbums = getUsersSavedAlbums();
        shuffle(savedAlbums);
        SavedAlbum album = getFirstElement(savedAlbums);
        playAlbumTracks(album);
    }

    public void playNewMusic() {
        String playlistId = getReleaseRadarPlaylistId(this.getSpotifyApi());
        playPlaylistsTracks(playlistId);
    }

    public void playRecommendations(String genre) {
        genre = genre.replace(" ", "-");
        TrackSimplified[] tracks = getUsersRecommendations(genre);
        shuffle(tracks);
        playMultipleTracksInARow(tracks);
    }

    public void playRandomTracksByArtist(String artistName) {
        Track[] tracks = getTracksByArtist(artistName);
        playMultipleTracksInARow(tracks);
    }

    public void playRandomAlbumByArtist(String artistName) {
        AlbumSimplified[] albums = getAlbumsByArtist(artistName);
        shuffle(albums);
        playAlbumTracks(getFirstAlbumFromArray(albums));
    }

    public void playTrackByQuery(String query) {
        if (query.isEmpty()) return;

        Track[] foundTracks = getTrackByQuery(query);
        Track track = getFirstElement(foundTracks);
        if (track!= null) playTrack(track.getId());
    }

    public void playAlbumByQuery(String query) {
        if (query.isEmpty()) return;

        AlbumSimplified[] foundAlbums = getAlbumByQuery(query);
        AlbumSimplified album = getFirstElement(foundAlbums);
        if (album!= null) playAlbumTracks(album);
    }

    private void playAlbumTracks(Object album) {
        String albumId = extractAlbumId(album);
        TrackSimplified[] tracks = getTracksFromAlbum(albumId);
        playMultipleTracksInARow(tracks);
    }

    private void playPlaylistsTracks(String playlistId) {
        PlaylistTrack[] tracks = getTracksFromPlaylist(playlistId);
        playMultipleTracksInARow(tracks);
    }

    private void playMultipleTracksInARow(Object[] tracks) {
        if (tracks.length == 0) return;

        String trackId = getIdFromTrack(tracks[0]);
        playTrack(trackId);

        for (int i = 1; i < tracks.length; i++) {
            String trackUri = getUriFromTrack(tracks[i]);
            addTrackToQueue(trackUri);
        }
    }

    private void playTrack(String trackId) {
        StartResumeUsersPlaybackRequest playRequest = this.getSpotifyApi().startResumeUsersPlayback()
                .uris(JsonParser.parseString("[\"spotify:track:" + trackId + "\"]").getAsJsonArray())
                .device_id(deviceId)
                .build();
        executeRequest(playRequest);
    }

    private void addTrackToQueue(String trackUri) {
        AddItemToUsersPlaybackQueueRequest addItemToUsersPlaybackQueueRequest = this.getSpotifyApi()
                .addItemToUsersPlaybackQueue(trackUri)
                .device_id(deviceId)
                .build();
        executeRequest(addItemToUsersPlaybackQueueRequest);
        stopThread(100);
    }

    public void pausePlaying() {
        PauseUsersPlaybackRequest pauseUsersPlaybackRequest = this.getSpotifyApi()
                .pauseUsersPlayback()
                .device_id(deviceId)
                .build();
        executeRequest(pauseUsersPlaybackRequest);
    }

    public void resumePlaying() {
        StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = this.getSpotifyApi()
                .startResumeUsersPlayback()
                .device_id(deviceId)
                .build();
        executeRequest(startResumeUsersPlaybackRequest);
    }

    public void nextTrack() {
        SkipUsersPlaybackToNextTrackRequest skipUsersPlaybackToNextTrackRequest = this.getSpotifyApi()
                .skipUsersPlaybackToNextTrack()
                .device_id(deviceId)
                .build();
        executeRequest(skipUsersPlaybackToNextTrackRequest);
    }

    public void previousTrack() {
        SkipUsersPlaybackToPreviousTrackRequest skipUsersPlaybackToPreviousTrackRequest = this.getSpotifyApi()
                .skipUsersPlaybackToPreviousTrack()
                .device_id(deviceId)
                .build();
        executeRequest(skipUsersPlaybackToPreviousTrackRequest);
        executeRequest(skipUsersPlaybackToPreviousTrackRequest);
    }

    public void setVolume(int volumePercent) {
        SetVolumeForUsersPlaybackRequest setVolumeForUsersPlaybackRequest = this.getSpotifyApi()
                .setVolumeForUsersPlayback(volumePercent)
                .device_id(deviceId)
                .build();
        executeRequest(setVolumeForUsersPlaybackRequest);
    }
}