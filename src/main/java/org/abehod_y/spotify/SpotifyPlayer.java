package org.abehod_y.spotify;


import com.google.gson.JsonParser;
import com.neovisionaries.i18n.CountryCode;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumsTracksRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import se.michaelthelin.spotify.requests.data.player.*;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;

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
        List<SavedTrack> savedTracks = this.getUserSavedTracks();
        Collections.shuffle(savedTracks);
        boolean isFirstTrack = true;
        for (SavedTrack savedTrack : savedTracks) {
            if (isFirstTrack) { playTrack(savedTrack.getTrack().getId()); isFirstTrack = false; }
            else addTrackToQueue(savedTrack.getTrack().getUri());
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
            boolean firstTrack = true;
            final Track[] tracks = getArtistsTopTracksRequest.execute();
            List<Track> trackList = Arrays.asList(tracks);
            Collections.shuffle(trackList);
            for (Track track : trackList) {
                if (firstTrack) {
                    playTrack(track.getId());
                    firstTrack = false;
                } else {
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
            AlbumSimplified[] albums = getArtistsAlbumsRequest.execute().getItems();
            List<AlbumSimplified> albumList = Arrays.asList(albums);
            Collections.shuffle(albumList);
            for (AlbumSimplified album : albumList) {
                if (album.getAlbumType().getType().equals("album")) {
                    playAlbumsTracks(album.getId());
                    break;
                }
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void playAlbumsTracks(String albumId) {
        final GetAlbumsTracksRequest getAlbumsTracksRequest = this.getSpotifyApi()
                .getAlbumsTracks(albumId)
                .build();
        try {
            final TrackSimplified[] trackSimplifiedPaging = getAlbumsTracksRequest.execute().getItems();
            boolean firstTrack = true;
            for (TrackSimplified track : trackSimplifiedPaging) {
                if (firstTrack) {
                    playTrack(track.getId());
                    firstTrack = false;
                } else {
                    addTrackToQueue(track.getUri());
                }
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    private String getArtistId(String artistName) {
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
}
