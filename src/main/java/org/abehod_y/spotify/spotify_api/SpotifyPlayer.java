package org.abehod_y.spotify.spotify_api;

import com.google.gson.JsonParser;
import com.neovisionaries.i18n.CountryCode;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.AbstractDataRequest;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumsTracksRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import se.michaelthelin.spotify.requests.data.library.GetCurrentUsersSavedAlbumsRequest;
import se.michaelthelin.spotify.requests.data.player.*;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.search.SearchItemRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchAlbumsRequest;
import se.michaelthelin.spotify.requests.AbstractRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.abehod_y.spotify.spotify_api.SpotifyIds.*;


public class SpotifyPlayer extends SpotifyLibrary {
    private final String deviceId;
    public SpotifyPlayer(String clientId, String clientSecret, String refreshToken, String deviceId) {
        super(clientId, clientSecret, refreshToken);
        this.deviceId = deviceId;
    }

    public void playRandomMusic() throws IOException, ParseException, SpotifyWebApiException {
        List<SavedTrack> savedTracks = Arrays.asList(getUserSavedTracks());
        Collections.shuffle(savedTracks);
        if (!savedTracks.isEmpty()) {
            SavedTrack firstTrack = savedTracks.get(0);
            playTrack(firstTrack.getTrack().getId());

            for (int i = 1; i < savedTracks.size(); i++) {
                SavedTrack track = savedTracks.get(i);
                addTrackToQueue(track.getTrack().getUri());
            }
        }
    }

    public void playRandomAlbum() throws IOException, ParseException, SpotifyWebApiException {
        final GetCurrentUsersSavedAlbumsRequest getCurrentUsersSavedAlbumsRequest = this.getSpotifyApi()
                .getCurrentUsersSavedAlbums()
                .limit(50)
                .build();
        List<SavedAlbum> savedAlbums = Arrays.asList(getCurrentUsersSavedAlbumsRequest.execute().getItems());
        Collections.shuffle(savedAlbums);
        Album album = savedAlbums.get(0).getAlbum();
        playAlbumsTracks(album.getId());
    }

    public void playNewMusic() throws IOException, ParseException, SpotifyWebApiException {
        String playlistId = getReleaseRadarPlaylistId(this.getSpotifyApi());
        playPlaylistsTracks(playlistId);
    }

    public void playRecommendations(String genre) throws IOException, ParseException, SpotifyWebApiException {
        genre = genre.replace(" ", "-");
        final GetRecommendationsRequest getRecommendationsRequest = this.getSpotifyApi()
                .getRecommendations()
                .seed_genres(genre)
                .target_popularity(100)
                .build();

        final List<TrackSimplified> recommendations = Arrays.asList(getRecommendationsRequest.execute().getTracks());
        Collections.shuffle(recommendations);
        if (!recommendations.isEmpty()) {
            TrackSimplified firstTrack = recommendations.get(0);
            playTrack(firstTrack.getId());

            for (TrackSimplified track : recommendations.subList(1, recommendations.size())) {
                addTrackToQueue(track.getUri());
            }
        }
    }

    public void playRandomTracksByArtist(String artistName) throws IOException, ParseException, SpotifyWebApiException {
        final GetArtistsTopTracksRequest getArtistsTopTracksRequest = this.getSpotifyApi()
                .getArtistsTopTracks(getArtistId(this.getSpotifyApi(), artistName), CountryCode.LT)
                .build();

        List<Track> tracks = Arrays.asList(getArtistsTopTracksRequest.execute());
        if (!tracks.isEmpty()) {
            Track firstTrack = tracks.get(0);
            playTrack(firstTrack.getId());

            for (Track track : tracks.subList(1, tracks.size())) {
                addTrackToQueue(track.getUri());
            }
        }
    }

    public void playRandomAlbumByArtist(String artistName) throws IOException, ParseException, SpotifyWebApiException {
        final GetArtistsAlbumsRequest getArtistsAlbumsRequest = this.getSpotifyApi()
                .getArtistsAlbums(getArtistId(this.getSpotifyApi(), artistName))
                .build();
        List<AlbumSimplified> albums = Arrays.asList(getArtistsAlbumsRequest.execute().getItems());
        Collections.shuffle(albums);
        albums.stream()
                .filter(album -> album.getAlbumType().getType().equals("album"))
                .findFirst()
                .ifPresent(album -> {
                    try {
                        playAlbumsTracks(album.getId());
                    } catch (IOException | ParseException | SpotifyWebApiException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                });
    }

    public void playAlbumsTracks(String albumId) throws IOException, ParseException, SpotifyWebApiException {
        final GetAlbumsTracksRequest getAlbumsTracksRequest = this.getSpotifyApi()
                .getAlbumsTracks(albumId)
                .limit(50)
                .build();
        final TrackSimplified[] tracks = getAlbumsTracksRequest.execute().getItems();
        if (tracks.length > 0) {
            TrackSimplified firstTrack = tracks[0];
            playTrack(firstTrack.getId());

            for (int i = 1; i < tracks.length; i++) {
                addTrackToQueue(tracks[i].getUri());
            }
        }
    }

    private void playPlaylistsTracks(String playlistId) throws IOException, ParseException, SpotifyWebApiException {
        final GetPlaylistsItemsRequest getPlaylistsItemsRequest = this.getSpotifyApi()
                .getPlaylistsItems(playlistId)
                .build();

        final PlaylistTrack[] tracks = getPlaylistsItemsRequest.execute().getItems();
        if (tracks.length != 0) {
            PlaylistTrack firstTrack = tracks[0];
            playTrack(firstTrack.getTrack().getId());

            for (int i = 1; i < tracks.length; i++) {
                PlaylistTrack track = tracks[i];
                addTrackToQueue(track.getTrack().getUri());
            }
        }
    }


    public void playTrackByQuery(String query) throws IOException, ParseException, SpotifyWebApiException {
        if (query.isEmpty()) return;
        final SearchItemRequest searchItemRequest = this.getSpotifyApi()
                .searchItem(query, ModelObjectType.TRACK.getType())
                .build();
        final Track[] searchResult = searchItemRequest
                .execute()
                .getTracks()
                .getItems();
        playTrack(searchResult[0].getId());
    }

    public void playAlbumByQuery(String query) throws IOException, ParseException, SpotifyWebApiException {
        if (query.isEmpty()) return;
        final SearchAlbumsRequest searchAlbumsRequest = this.getSpotifyApi()
                .searchAlbums(query)
                .build();
        final AlbumSimplified[] album = searchAlbumsRequest
                .execute()
                .getItems();
        playAlbumsTracks(album[0].getId());
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

    private void executeRequest(AbstractRequest<?> request) {
        try {
            request.execute();
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
