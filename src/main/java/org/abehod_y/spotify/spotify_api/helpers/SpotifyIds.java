package org.abehod_y.spotify.spotify_api.helpers;

import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchPlaylistsRequest;

import java.io.IOException;

public class SpotifyIds {

    public static String getCurrentlyPlayingTrackId(SpotifyApi spotifyApi) throws IOException, ParseException, SpotifyWebApiException {
        final GetUsersCurrentlyPlayingTrackRequest getUsersCurrentlyPlayingTrackRequest = spotifyApi
                .getUsersCurrentlyPlayingTrack()
                .build();
        final CurrentlyPlaying currentlyPlaying = getUsersCurrentlyPlayingTrackRequest.execute();
        String trackId = null;
        if (currentlyPlaying.getIs_playing()) {
            trackId = currentlyPlaying.getItem().getId();
        }
        return trackId;
    }

    public static String getArtistId(SpotifyApi spotifyApi, String artistName) throws IOException, ParseException, SpotifyWebApiException {
        final SearchArtistsRequest searchArtistsRequest = spotifyApi
                .searchArtists(artistName)
                .build();
        final Paging<Artist> artistPaging = searchArtistsRequest.execute();
        Artist artist = artistPaging.getItems()[0];
        return artist.getId();
    }

    public static String getReleaseRadarPlaylistId(SpotifyApi spotifyApi) throws IOException, ParseException, SpotifyWebApiException {
        final SearchPlaylistsRequest searchPlaylistRequest = spotifyApi
                .searchPlaylists("Release Radar")
                .build();
        PlaylistSimplified[] playlists = searchPlaylistRequest
                .execute()
                .getItems();

        return playlists[0].getId();
    }
}
