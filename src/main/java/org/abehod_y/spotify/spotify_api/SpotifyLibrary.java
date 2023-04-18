package org.abehod_y.spotify.spotify_api;

import com.neovisionaries.i18n.CountryCode;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.special.SearchResult;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumsTracksRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import se.michaelthelin.spotify.requests.data.library.GetCurrentUsersSavedAlbumsRequest;
import se.michaelthelin.spotify.requests.data.library.GetUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.library.RemoveUsersSavedTracksRequest;
import se.michaelthelin.spotify.requests.data.library.SaveTracksForUserRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.search.SearchItemRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchAlbumsRequest;

import static org.abehod_y.spotify.spotify_api.helpers.ArraysHelpers.getItemsOrNull;
import static org.abehod_y.spotify.spotify_api.helpers.Requests.*;
import static org.abehod_y.spotify.spotify_api.helpers.SpotifyItemsIds.getArtistId;

public class SpotifyLibrary extends SpotifyBuilder {

    SpotifyLibrary(String clientId, String clientSecret, String refreshToken) {
        super(clientId, clientSecret, refreshToken);
    }

    SavedTrack[] getUserSavedTracks() {
        GetUsersSavedTracksRequest getUsersSavedTracksRequest = this.getSpotifyApi().getUsersSavedTracks()
                .limit(50)
                .offset(0)
                .build();

        Paging<SavedTrack> savedTracks = executeRequestWithReturn(getUsersSavedTracksRequest);
        return getItemsOrNull(savedTracks);
    }

    SavedAlbum[] getUsersSavedAlbums() {
        GetCurrentUsersSavedAlbumsRequest getCurrentUsersSavedAlbumsRequest = this.getSpotifyApi()
                .getCurrentUsersSavedAlbums()
                .limit(50)
                .build();

        Paging<SavedAlbum> savedAlbums = executeRequestWithReturn(getCurrentUsersSavedAlbumsRequest);
        return getItemsOrNull(savedAlbums);
    }

    TrackSimplified[] getUsersRecommendations(String genre) {
        GetRecommendationsRequest getRecommendationsRequest = this.getSpotifyApi()
                .getRecommendations()
                .seed_genres(genre)
                .target_popularity(100)
                .build();

        Recommendations recommendations = executeRequestWithReturn(getRecommendationsRequest);
        return getItemsOrNull(recommendations);
    }

    Track[] getTracksByArtist(String name) {
        GetArtistsTopTracksRequest getArtistsTopTracksRequest = this.getSpotifyApi()
                .getArtistsTopTracks(getArtistId(this.getSpotifyApi(), name), CountryCode.LT)
                .build();

        return executeRequestWithArrayReturn(getArtistsTopTracksRequest);
    }

    AlbumSimplified[] getAlbumsByArtist(String name) {
        GetArtistsAlbumsRequest getArtistsAlbumsRequest = this.getSpotifyApi()
                .getArtistsAlbums(getArtistId(this.getSpotifyApi(), name))
                .build();

        Paging<AlbumSimplified> albums = executeRequestWithReturn(getArtistsAlbumsRequest);
        return getItemsOrNull(albums);
    }

    public TrackSimplified[] getTracksFromAlbum(String albumId) {
        GetAlbumsTracksRequest getAlbumsTracksRequest = this.getSpotifyApi()
                .getAlbumsTracks(albumId)
                .limit(50)
                .build();

        Paging<TrackSimplified> tracks = executeRequestWithReturn(getAlbumsTracksRequest);
        return getItemsOrNull(tracks);
    }

    public PlaylistTrack[] getTracksFromPlaylist(String playlistId) {
        GetPlaylistsItemsRequest getPlaylistsItemsRequest = this.getSpotifyApi()
                .getPlaylistsItems(playlistId)
                .build();

        Paging<PlaylistTrack> tracks = executeRequestWithReturn(getPlaylistsItemsRequest);
        return getItemsOrNull(tracks);
    }

    public Track[] getTrackByQuery(String query) {
        SearchItemRequest searchItemRequest = this.getSpotifyApi()
                .searchItem(query, ModelObjectType.TRACK.getType())
                .build();

        SearchResult tracks = executeRequestWithReturn(searchItemRequest);
        return getItemsOrNull(tracks);
    }

    public AlbumSimplified[] getAlbumByQuery(String query) {
        SearchAlbumsRequest searchAlbumsRequest = this.getSpotifyApi()
                .searchAlbums(query)
                .build();

        Paging<AlbumSimplified> albums = executeRequestWithReturn(searchAlbumsRequest);
        return getItemsOrNull(albums);
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
