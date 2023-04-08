package org.abehod_y.spotify;

import com.google.gson.JsonParser;
import com.neovisionaries.i18n.CountryCode;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.abehod_y.spotify.helpers.SpotifyIds.*;


public class SpotifyPlayer extends SpotifyLibrary {

    public SpotifyPlayer(String clientId, String clientSecret, String deviceId, String refreshToken) {
        super(clientId, clientSecret, deviceId,  refreshToken);
    }

    public void playSomeMusic() throws IOException, ParseException, SpotifyWebApiException {
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

    public void pausePlaying() throws IOException, ParseException, SpotifyWebApiException {
        final PauseUsersPlaybackRequest pauseUsersPlaybackRequest = this.getSpotifyApi()
                .pauseUsersPlayback()
                .build();
        pauseUsersPlaybackRequest.execute();
    }

    public void resumePlaying() throws IOException, ParseException, SpotifyWebApiException {
        final StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = this.getSpotifyApi()
                .startResumeUsersPlayback()
                .build();
        startResumeUsersPlaybackRequest.execute();
    }

    public void nextTrack() throws IOException, ParseException, SpotifyWebApiException {
        final SkipUsersPlaybackToNextTrackRequest skipUsersPlaybackToNextTrackRequest = this.getSpotifyApi()
                .skipUsersPlaybackToNextTrack()
                .build();
        skipUsersPlaybackToNextTrackRequest.execute();
    }

    public void previousTrack() throws IOException, ParseException, SpotifyWebApiException {
        final SkipUsersPlaybackToPreviousTrackRequest skipUsersPlaybackToPreviousTrackRequest = this.getSpotifyApi()
                .skipUsersPlaybackToPreviousTrack()
                .build();
        skipUsersPlaybackToPreviousTrackRequest.execute();
    }

    public void setVolume(int volumePercent) throws IOException, ParseException, SpotifyWebApiException {
        final SetVolumeForUsersPlaybackRequest setVolumeForUsersPlaybackRequest = this.getSpotifyApi()
                .setVolumeForUsersPlayback(volumePercent)
                .build();
        setVolumeForUsersPlaybackRequest.execute();
    }

    public void playTracksByArtist(String artistName) throws IOException, ParseException, SpotifyWebApiException {
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

    public void playAlbumByArtist(String artistName) throws IOException, ParseException, SpotifyWebApiException {
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

    public void playTrack(String trackId) throws IOException, ParseException, SpotifyWebApiException {
        StartResumeUsersPlaybackRequest playRequest = this.getSpotifyApi().startResumeUsersPlayback()
                .uris(JsonParser.parseString("[\"spotify:track:" + trackId + "\"]").getAsJsonArray())
                .device_id(this.getDeviceId())
                .build();
        playRequest.execute();
    }

    public void addTrackToQueue(String trackUri) throws IOException, ParseException, SpotifyWebApiException {
        final AddItemToUsersPlaybackQueueRequest addItemToUsersPlaybackQueueRequest = this.getSpotifyApi()
                .addItemToUsersPlaybackQueue(trackUri)
                .build();
        addItemToUsersPlaybackQueueRequest.execute();
    }

    public void playNewMusic() throws IOException, ParseException, SpotifyWebApiException {
        String playlistId = getReleaseRadarPlaylistId(this.getSpotifyApi());
        playPlaylistsTracks(playlistId);
    }

    private void playPlaylistsTracks(String playlistId) throws IOException, ParseException, SpotifyWebApiException {
        final GetPlaylistsItemsRequest getPlaylistsItemsRequest = this.getSpotifyApi()
                .getPlaylistsItems(playlistId)
                .build();

        final List<PlaylistTrack> tracks = Arrays.asList(getPlaylistsItemsRequest.execute().getItems());
        Collections.shuffle(tracks);
        if (!tracks.isEmpty()) {
            PlaylistTrack firstTrack = tracks.get(0);
            playTrack(firstTrack.getTrack().getId());

            for (PlaylistTrack track : tracks.subList(1, tracks.size())) {
                addTrackToQueue(track.getTrack().getUri());
            }
        }
    }

    public void playSomeAlbum() throws IOException, ParseException, SpotifyWebApiException {
        final GetCurrentUsersSavedAlbumsRequest getCurrentUsersSavedAlbumsRequest = this.getSpotifyApi()
                .getCurrentUsersSavedAlbums()
                .limit(50)
                .build();
        List<SavedAlbum> savedAlbums = Arrays.asList(getCurrentUsersSavedAlbumsRequest.execute().getItems());
        Collections.shuffle(savedAlbums);
        playAlbumsTracks(savedAlbums.get(0).getAlbum().getId());
    }

    public void playTrackByQuery(String query) throws IOException, ParseException, SpotifyWebApiException {
        if (query.isEmpty()) return;
        final SearchItemRequest searchItemRequest = this.getSpotifyApi()
                .searchItem(query, ModelObjectType.TRACK.getType())
                .build();
        final Track[] searchResult = searchItemRequest.execute().getTracks().getItems();
        playTrack(searchResult[0].getId());
    }

    public void playAlbumByQuery(String query) throws IOException, ParseException, SpotifyWebApiException {
        if (query.isEmpty()) return;
        final SearchAlbumsRequest searchAlbumsRequest = this.getSpotifyApi()
                .searchAlbums(query)
                .build();
        final AlbumSimplified[] album = searchAlbumsRequest.execute().getItems();
        playAlbumsTracks(album[0].getId());
    }
}
