package org.abehod_y.spotify;

import com.google.gson.JsonParser;
import com.neovisionaries.i18n.CountryCode;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumsTracksRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import se.michaelthelin.spotify.requests.data.player.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SpotifyPlayer extends SpotifyLibrary {

    public SpotifyPlayer(String clientId, String clientSecret,
                         String deviceId, String accessToken, String refreshToken) {
        super(clientId, clientSecret, deviceId, accessToken, refreshToken);
    }

    public void playSomething() {
        List<SavedTrack> savedTracks = Arrays.asList(getUserSavedTracks());
        Collections.shuffle(savedTracks);
        if (!savedTracks.isEmpty()) {
            SavedTrack firstTrack = savedTracks.get(0);
            playTrack(firstTrack.getTrack().getId());

            for (SavedTrack track : savedTracks.subList(1, savedTracks.size())) {
                addTrackToQueue(track.getTrack().getUri());
            }
        }

    }

    public void playRecommendations(String genre) {
        genre = genre.replace(" ", "-");
        final GetRecommendationsRequest getRecommendationsRequest = this.getSpotifyApi()
                .getRecommendations()
                .seed_genres(genre)
                .target_popularity(100)
                .build();
        try {
            final List<TrackSimplified> recommendations = Arrays.asList(getRecommendationsRequest.execute().getTracks());
            Collections.shuffle(recommendations);
            if (!recommendations.isEmpty()) {
                TrackSimplified firstTrack = recommendations.get(0);
                playTrack(firstTrack.getId());

                for (TrackSimplified track : recommendations.subList(1, recommendations.size())) {
                    addTrackToQueue(track.getUri());
                }
            }
        } catch (IOException | SpotifyWebApiException | ParseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void pausePlaying() {
        final PauseUsersPlaybackRequest pauseUsersPlaybackRequest = this.getSpotifyApi()
                .pauseUsersPlayback()
                .build();
        try {
            pauseUsersPlaybackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void resumePlaying() {
        final StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = this.getSpotifyApi()
                .startResumeUsersPlayback()
                .build();
        try {
            startResumeUsersPlaybackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void nextTrack() {
        final SkipUsersPlaybackToNextTrackRequest skipUsersPlaybackToNextTrackRequest = this.getSpotifyApi()
                .skipUsersPlaybackToNextTrack()
                .build();
        try {
            skipUsersPlaybackToNextTrackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void previousTrack() {
        final SkipUsersPlaybackToPreviousTrackRequest skipUsersPlaybackToPreviousTrackRequest = this.getSpotifyApi()
                .skipUsersPlaybackToPreviousTrack()
                .build();
        try {
            skipUsersPlaybackToPreviousTrackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void setVolume(int volumePercent) {
        final SetVolumeForUsersPlaybackRequest setVolumeForUsersPlaybackRequest = this.getSpotifyApi()
                .setVolumeForUsersPlayback(volumePercent)
                .build();
        try {
            setVolumeForUsersPlaybackRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void playTracksByArtist(String artistName) {
        final GetArtistsTopTracksRequest getArtistsTopTracksRequest = this.getSpotifyApi()
                .getArtistsTopTracks(getArtistId(artistName), CountryCode.LT)
                .build();
        try {
            List<Track> tracks = Arrays.asList(getArtistsTopTracksRequest.execute());
            if (!tracks.isEmpty()) {
                Track firstTrack = tracks.get(0);
                playTrack(firstTrack.getId());

                for (Track track : tracks.subList(1, tracks.size())) {
                    addTrackToQueue(track.getUri());
                }
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void playAlbumByArtist(String artistName) {
        final GetArtistsAlbumsRequest getArtistsAlbumsRequest = this.getSpotifyApi()
                .getArtistsAlbums(getArtistId(artistName))
                .build();
        try {
            List<AlbumSimplified> albums = Arrays.asList(getArtistsAlbumsRequest.execute().getItems());
            Collections.shuffle(albums);
            albums.stream()
                    .filter(album -> album.getAlbumType().getType().equals("album"))
                    .findFirst()
                    .ifPresent(album -> playAlbumsTracks(album.getId()));
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void playAlbumsTracks(String albumId) {
        final GetAlbumsTracksRequest getAlbumsTracksRequest = this.getSpotifyApi()
                .getAlbumsTracks(albumId)
                .build();
        try {
            final TrackSimplified[] tracks = getAlbumsTracksRequest.execute().getItems();
            if (tracks.length > 0) {
                TrackSimplified firstTrack = tracks[0];
                playTrack(firstTrack.getId());

                for (int i = 1; i < tracks.length; i++) {
                    addTrackToQueue(tracks[i].getUri());
                }
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    public void playTrack(String trackId) {
        StartResumeUsersPlaybackRequest playRequest = this.getSpotifyApi().startResumeUsersPlayback()
                .uris(JsonParser.parseString("[\"spotify:track:" + trackId + "\"]").getAsJsonArray())
                .device_id(this.getDeviceId())
                .build();
        try {
            playRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void addTrackToQueue(String trackUri) {
        final AddItemToUsersPlaybackQueueRequest addItemToUsersPlaybackQueueRequest = this.getSpotifyApi()
                .addItemToUsersPlaybackQueue(trackUri)
                .build();
        try {
            addItemToUsersPlaybackQueueRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
