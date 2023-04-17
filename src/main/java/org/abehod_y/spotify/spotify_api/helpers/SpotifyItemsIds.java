package org.abehod_y.spotify.spotify_api.helpers;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchPlaylistsRequest;

import java.util.Objects;

import static org.abehod_y.spotify.spotify_api.helpers.Requests.*;

public class SpotifyItemsIds {

    public static String getCurrentlyPlayingTrackId(SpotifyApi spotifyApi) {
        final GetUsersCurrentlyPlayingTrackRequest getUsersCurrentlyPlayingTrackRequest = spotifyApi
                .getUsersCurrentlyPlayingTrack()
                .build();
        final CurrentlyPlaying currentlyPlaying = executeRequestWithReturn(getUsersCurrentlyPlayingTrackRequest);
        String trackId = null;
        assert currentlyPlaying != null;
        if (currentlyPlaying.getIs_playing()) {
            trackId = currentlyPlaying.getItem().getId();
        }
        return trackId;
    }

    public static String getArtistId(SpotifyApi spotifyApi, String artistName) {
        final SearchArtistsRequest searchArtistsRequest = spotifyApi
                .searchArtists(artistName)
                .build();
        final Paging<Artist> artistPaging = executeRequestWithReturn(searchArtistsRequest);
        assert artistPaging != null;
        Artist artist = artistPaging.getItems()[0];
        return artist.getId();
    }

    public static String getReleaseRadarPlaylistId(SpotifyApi spotifyApi) {
        final SearchPlaylistsRequest searchPlaylistRequest = spotifyApi
                .searchPlaylists("Release Radar")
                .build();
        PlaylistSimplified[] playlists = Objects.requireNonNull(executeRequestWithReturn(searchPlaylistRequest))
                .getItems();

        return playlists[0].getId();
    }
}
