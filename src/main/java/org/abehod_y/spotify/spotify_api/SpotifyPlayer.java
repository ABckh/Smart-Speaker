package org.abehod_y.spotify.spotify_api;

import com.google.gson.JsonParser;
import com.neovisionaries.i18n.CountryCode;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumsTracksRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import se.michaelthelin.spotify.requests.data.library.GetCurrentUsersSavedAlbumsRequest;
import se.michaelthelin.spotify.requests.data.player.*;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.search.SearchItemRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchAlbumsRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.abehod_y.spotify.spotify_api.helpers.SpotifyItemsIds.*;
import static org.abehod_y.spotify.spotify_api.helpers.Requests.*;


public class SpotifyPlayer extends SpotifyLibrary {
    private final String deviceId;
    public SpotifyPlayer(String clientId, String clientSecret, String refreshToken, String deviceId) {
        super(clientId, clientSecret, refreshToken);
        this.deviceId = deviceId;
    }

    public void playRandomMusic() {
        List<SavedTrack> savedTracks = Arrays.asList(getUserSavedTracks());
        Collections.shuffle(savedTracks);
//        playFewSongsInARows(savedTracks);
        if (!savedTracks.isEmpty()) {
            SavedTrack firstTrack = savedTracks.get(0);
            playTrack(firstTrack.getTrack().getId());

            for (int i = 1; i < savedTracks.size(); i++) {
                SavedTrack track = savedTracks.get(i);
                addTrackToQueue(track.getTrack().getUri());
            }
        }
    }

//    private <T extends AbstractModelObject> void playFewSongsInARows(List<T> tracks) {
//        if (!tracks.isEmpty()) {
//            T firstTrack = tracks.get(0);
//            playTrack(firstTrack.getTrack().getId());
//
//            for (int i = 1; i < tracks.size(); i++) {
//                var track = tracks.get(i);
//                addTrackToQueue(track.getTrack().getUri());
//            }
//        }
//    }

    public void playRandomAlbum() {
        final GetCurrentUsersSavedAlbumsRequest getCurrentUsersSavedAlbumsRequest = this.getSpotifyApi()
                .getCurrentUsersSavedAlbums()
                .limit(50)
                .build();
        List<SavedAlbum> savedAlbums = Arrays.asList(Objects.requireNonNull(executeRequestWithReturn(getCurrentUsersSavedAlbumsRequest)).getItems());
        Collections.shuffle(savedAlbums);
        Album album = savedAlbums.get(0).getAlbum();
        playAlbumsTracks(album.getId());
    }

    public void playNewMusic() {
        String playlistId = getReleaseRadarPlaylistId(this.getSpotifyApi());
        playPlaylistsTracks(playlistId);
    }

    public void playRecommendations(String genre) {
        genre = genre.replace(" ", "-");
        final GetRecommendationsRequest getRecommendationsRequest = this.getSpotifyApi()
                .getRecommendations()
                .seed_genres(genre)
                .target_popularity(100)
                .build();

        final List<TrackSimplified> recommendations = Arrays.asList(Objects.requireNonNull(executeRequestWithReturn(getRecommendationsRequest)).getTracks());
        Collections.shuffle(recommendations);
        if (!recommendations.isEmpty()) {
            TrackSimplified firstTrack = recommendations.get(0);
            playTrack(firstTrack.getId());

            for (int i = 1; i < recommendations.size(); i++) {
                TrackSimplified track = recommendations.get(i);
                addTrackToQueue(track.getUri());
            }
        }
    }

    public void playRandomTracksByArtist(String artistName) {
        final GetArtistsTopTracksRequest getArtistsTopTracksRequest = this.getSpotifyApi()
                .getArtistsTopTracks(getArtistId(this.getSpotifyApi(), artistName), CountryCode.LT)
                .build();

        List<Track> tracks = Arrays.asList(Objects.requireNonNull(executeRequestWithArrayReturn(getArtistsTopTracksRequest)));
        if (!tracks.isEmpty()) {
            Track firstTrack = tracks.get(0);
            playTrack(firstTrack.getId());

            for (int i = 1; i < tracks.size(); i++) {
                Track track = tracks.get(0);
                addTrackToQueue(track.getUri());
            }
        }
    }


    public void playRandomAlbumByArtist(String artistName) {
        final GetArtistsAlbumsRequest getArtistsAlbumsRequest = this.getSpotifyApi()
                .getArtistsAlbums(getArtistId(this.getSpotifyApi(), artistName))
                .build();
        List<AlbumSimplified> albums = Arrays.asList(Objects.requireNonNull(executeRequestWithReturn(getArtistsAlbumsRequest)).getItems());
        Collections.shuffle(albums);
        albums.stream()
                .filter(album -> album.getAlbumType().getType().equals("album"))
                .findFirst()
                .ifPresent(album -> playAlbumsTracks(album.getId()));
    }

    public void playAlbumsTracks(String albumId) {
        final GetAlbumsTracksRequest getAlbumsTracksRequest = this.getSpotifyApi()
                .getAlbumsTracks(albumId)
                .limit(50)
                .build();
        final TrackSimplified[] tracks = Objects.requireNonNull(executeRequestWithReturn(getAlbumsTracksRequest)).getItems();
        if (tracks.length > 0) {
            TrackSimplified firstTrack = tracks[0];
            playTrack(firstTrack.getId());

            for (int i = 1; i < tracks.length; i++) {
                addTrackToQueue(tracks[i].getUri());
            }
        }
    }

    private void playPlaylistsTracks(String playlistId) {
        final GetPlaylistsItemsRequest getPlaylistsItemsRequest = this.getSpotifyApi()
                .getPlaylistsItems(playlistId)
                .build();

        final PlaylistTrack[] tracks = Objects.requireNonNull(executeRequestWithReturn(getPlaylistsItemsRequest)).getItems();
        if (tracks.length != 0) {
            PlaylistTrack firstTrack = tracks[0];
            playTrack(firstTrack.getTrack().getId());

            for (int i = 1; i < tracks.length; i++) {
                PlaylistTrack track = tracks[i];
                addTrackToQueue(track.getTrack().getUri());
            }
        }
    }


    public void playTrackByQuery(String query) {
        if (query.isEmpty()) return;
        final SearchItemRequest searchItemRequest = this.getSpotifyApi()
                .searchItem(query, ModelObjectType.TRACK.getType())
                .build();
        final Track[] searchResult = Objects.requireNonNull(executeRequestWithReturn(searchItemRequest))
                .getTracks()
                .getItems();
        playTrack(searchResult[0].getId());
    }

    public void playAlbumByQuery(String query) {
        if (query.isEmpty()) return;
        final SearchAlbumsRequest searchAlbumsRequest = this.getSpotifyApi()
                .searchAlbums(query)
                .build();
        AlbumSimplified[] albums = Objects.requireNonNull(executeRequestWithReturn(searchAlbumsRequest))
                .getItems();
        playAlbumsTracks(albums[0].getId());
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
