package org.abehod_y.spotify.spotify_api.helpers;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchPlaylistsRequest;

import java.util.Objects;

import static org.abehod_y.spotify.spotify_api.helpers.ArraysHelpers.getFirstElement;
import static org.abehod_y.spotify.spotify_api.helpers.ArraysHelpers.getItemsOrNull;
import static org.abehod_y.spotify.spotify_api.helpers.Requests.*;

public class SpotifyItemsIds {
    private SpotifyItemsIds() {}

    public static String getCurrentlyPlayingTrackId(SpotifyApi spotifyApi) {
        GetUsersCurrentlyPlayingTrackRequest getUsersCurrentlyPlayingTrackRequest = spotifyApi
                .getUsersCurrentlyPlayingTrack()
                .build();
        CurrentlyPlaying currentlyPlaying = executeRequestWithDataReturn(getUsersCurrentlyPlayingTrackRequest);
        return currentlyPlaying != null && currentlyPlaying.getIs_playing() ? currentlyPlaying.getItem().getId() : null;
    }

    public static String getArtistId(SpotifyApi spotifyApi, String artistName) {
        SearchArtistsRequest searchArtistsRequest = spotifyApi
                .searchArtists(artistName)
                .build();
        Paging<Artist> artistPaging = executeRequestWithDataReturn(searchArtistsRequest);
        Artist artist = getFirstElement(Objects.requireNonNull(getItemsOrNull(artistPaging)));
        return artist != null ? artist.getId() : null;
    }

    public static String getReleaseRadarPlaylistId(SpotifyApi spotifyApi) {
        SearchPlaylistsRequest searchPlaylistRequest = spotifyApi
                .searchPlaylists("Release Radar")
                .build();
        Paging<PlaylistSimplified> playlists = executeRequestWithDataReturn(searchPlaylistRequest);
        PlaylistSimplified playlist = getFirstElement(Objects.requireNonNull(getItemsOrNull(playlists)));
        return playlist != null ? playlist.getId() : null;
    }

    public static String extractAlbumId(Object album) {
        if (album instanceof Album) return ((Album) album).getId();
        else if (album instanceof AlbumSimplified) return ((AlbumSimplified) album).getId();
        else if (album instanceof SavedAlbum) return ((SavedAlbum) album).getAlbum().getId();
        return null;
    }

    public static String getIdFromTrack(Object track) {
        if (track instanceof Track) return ((Track) track).getId();
        else if (track instanceof TrackSimplified) return ((TrackSimplified) track).getId();
        else if (track instanceof SavedTrack) return ((SavedTrack) track).getTrack().getId();
        else if (track instanceof PlaylistTrack) return ((PlaylistTrack) track).getTrack().getId();
        return null;
    }

    public static String getUriFromTrack(Object track) {
        if (track instanceof Track) return ((Track) track).getUri();
        else if (track instanceof TrackSimplified) return ((TrackSimplified) track).getUri();
        else if (track instanceof SavedTrack) return ((SavedTrack) track).getTrack().getUri();
        else if (track instanceof PlaylistTrack) return ((PlaylistTrack) track).getTrack().getUri();
        return null;
    }
}
