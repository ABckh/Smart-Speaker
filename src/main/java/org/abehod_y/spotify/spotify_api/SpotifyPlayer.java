package org.abehod_y.spotify.spotify_api;

import com.google.gson.JsonParser;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.player.*;

import static org.abehod_y.spotify.spotify_api.helpers.ArraysHelpers.shuffle;
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

        if (savedTracks.length > 0) {
            playTrack(savedTracks[0].getTrack().getId());
            for (int i = 1; i < savedTracks.length; i++) {
                addTrackToQueue(savedTracks[i].getTrack().getUri());
            }
        }
    }

    public void playRandomAlbum() {
        SavedAlbum[] savedAlbums = getUsersSavedAlbums();
        shuffle(savedAlbums);

        if (savedAlbums.length > 0) {
            playAlbumTracks(savedAlbums[0].getAlbum());
        }
    }

    public void playNewMusic() {
        String playlistId = getReleaseRadarPlaylistId(this.getSpotifyApi());
        playPlaylistsTracks(playlistId);
    }

    public void playRecommendations(String genre) {
        genre = genre.replace(" ", "-");
        TrackSimplified[] tracks = getUsersRecommendations(genre);
        shuffle(tracks);

        if (tracks.length > 0) {
            playTrack(tracks[0].getId());
            for (int i = 1; i < tracks.length; i++) {
                addTrackToQueue(tracks[i].getUri());
            }
        }
    }

    public void playRandomTracksByArtist(String artistName) {
        Track[] tracks = getTracksByArtist(artistName);

        if (tracks.length > 0) {
            playTrack(tracks[0].getId());
            for (int i = 1; i < tracks.length; i++) {
                addTrackToQueue(tracks[i].getUri());
            }
        }
    }

    public void playRandomAlbumByArtist(String artistName) {
        AlbumSimplified[] albums = getAlbumsByArtist(artistName);
        shuffle(albums);

        for (AlbumSimplified album : albums) {
            if (album.getAlbumType().getType().equals("album")) {
                playAlbumTracks(album);
                return;
            }
        }
    }

    public void playTrackByQuery(String query) {
        if (query.isEmpty()) return;
        Track[] foundTracks = getTrackByQuery(query);

        if (foundTracks.length > 0) {
            playTrack(foundTracks[0].getId());
        }
    }

    public void playAlbumByQuery(String query) {
        if (query.isEmpty()) return;
        AlbumSimplified[] foundAlbums = getAlbumByQuery(query);

        if (foundAlbums.length > 0) {
            playAlbumTracks(foundAlbums[0].getId());
        }
    }

    private void playAlbumTracks(AlbumSimplified album) {
        playAlbumTracks(album.getId());
    }

    private void playAlbumTracks(Album album) {
        playAlbumTracks(album.getId());
    }

    private void playAlbumTracks(String albumId) {
        TrackSimplified[] tracks = getTracksFromAlbum(albumId);
        if (tracks.length > 0) {
            playTrack(tracks[0].getId());

            for (int i = 1; i < tracks.length; i++) {
                addTrackToQueue(tracks[i].getUri());
            }
        }
    }

    private void playPlaylistsTracks(String playlistId) {
        PlaylistTrack[] tracks = getTracksFromPlaylist(playlistId);
        if (tracks.length > 0) {
            playTrack(tracks[0].getTrack().getId());

            for (int i = 1; i < tracks.length; i++) {
                addTrackToQueue(tracks[i].getTrack().getUri());
            }
        }
    }

    public void playTrack(String trackId) {
        StartResumeUsersPlaybackRequest playRequest = this.getSpotifyApi().startResumeUsersPlayback()
                .uris(JsonParser.parseString("[\"spotify:track:" + trackId + "\"]").getAsJsonArray())
                .device_id(deviceId)
                .build();
        executeRequest(playRequest);
    }

    public void addTrackToQueue(String trackUri) {
        final AddItemToUsersPlaybackQueueRequest addItemToUsersPlaybackQueueRequest = this.getSpotifyApi()
                .addItemToUsersPlaybackQueue(trackUri)
                .device_id(deviceId)
                .build();
        executeRequest(addItemToUsersPlaybackQueueRequest);
    }

    public void pausePlaying() {
        final PauseUsersPlaybackRequest pauseUsersPlaybackRequest = this.getSpotifyApi()
                .pauseUsersPlayback()
                .device_id(deviceId)
                .build();
        executeRequest(pauseUsersPlaybackRequest);
    }

    public void resumePlaying() {
        final StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = this.getSpotifyApi()
                .startResumeUsersPlayback()
                .device_id(deviceId)
                .build();
        executeRequest(startResumeUsersPlaybackRequest);
    }

    public void nextTrack() {
        final SkipUsersPlaybackToNextTrackRequest skipUsersPlaybackToNextTrackRequest = this.getSpotifyApi()
                .skipUsersPlaybackToNextTrack()
                .device_id(deviceId)
                .build();
        executeRequest(skipUsersPlaybackToNextTrackRequest);
    }

    public void previousTrack() {
        final SkipUsersPlaybackToPreviousTrackRequest skipUsersPlaybackToPreviousTrackRequest = this.getSpotifyApi()
                .skipUsersPlaybackToPreviousTrack()
                .device_id(deviceId)
                .build();
        executeRequest(skipUsersPlaybackToPreviousTrackRequest);
    }

    public void setVolume(int volumePercent) {
        final SetVolumeForUsersPlaybackRequest setVolumeForUsersPlaybackRequest = this.getSpotifyApi()
                .setVolumeForUsersPlayback(volumePercent)
                .device_id(deviceId)
                .build();
        executeRequest(setVolumeForUsersPlaybackRequest);
    }
}