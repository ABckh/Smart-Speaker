package org.abehod_y.spotify;

import ai.picovoice.cheetah.CheetahException;
import org.abehod_y.picovoice.PicovoiceRunner;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.Map;

import static org.abehod_y.spotify.helpers.SpotifyIds.getCurrentlyPlayingTrackId;

public class SpotifyRunner {

    private Map<String, String> slots;
    private final PicovoiceRunner picovoiceRunner;
    private final SpotifyPlayer spotifyPlayer;

    public SpotifyRunner(String clientId, String clientSecret, String deviceId, String refreshToken, PicovoiceRunner picovoiceRunner) {
        this.spotifyPlayer = new SpotifyPlayer(clientId, clientSecret, deviceId, refreshToken);
        this.picovoiceRunner = picovoiceRunner;
    }

    public void setSlots(Map<String, String> slots) {
        this.slots = slots;
    }

    public void runTaskFromIntent(String intent) throws IOException, ParseException, SpotifyWebApiException, CheetahException {
        switch (intent) {
            case "PlaySomeMusic" -> this.playSomeMusic();
            case "Pause" -> this.pausePlayback();
            case "Resume" -> this.resumePlayback();
            case "ChangeTrack" -> this.changeTrack();
            case "AddRemoveTrack" -> this.addRemoveTrack();
            case "SetVolume" -> this.setVolume();
            case "PlayByArtist" -> this.playByArtist();
            case "PlayNewMusic" -> this.playNewMusic();
            case "PlayConcreteSongOrAlbum" -> this.playConcreteSongOrAlbum();
        }
    }

    private void playSomeMusic() throws IOException, ParseException, SpotifyWebApiException {
        System.out.println("Playing music on spotify");
        if (slots.containsKey("genre")) {
            spotifyPlayer.playRecommendations(slots.get("genre"));
        } else if (slots.containsKey("item")) {
            if (slots.get("item").equals("music")) spotifyPlayer.playSomeMusic();
            else spotifyPlayer.playSomeAlbum();
        }
    }

    private void pausePlayback() throws IOException, ParseException, SpotifyWebApiException {
        System.out.println("Stopping...");
        spotifyPlayer.pausePlaying();
    }

    private void resumePlayback() throws IOException, ParseException, SpotifyWebApiException {
        System.out.println("Resuming...");
        spotifyPlayer.resumePlaying();
    }

    private void changeTrack() throws IOException, ParseException, SpotifyWebApiException {
        if (slots.containsKey("pointer")) {
            System.out.println("Changing track...");
            if (slots.get("pointer").equals("next")) spotifyPlayer.nextTrack();
            else spotifyPlayer.previousTrack();
        }
    }

    private void addRemoveTrack() throws IOException, ParseException, SpotifyWebApiException {
        if (slots.containsKey("action")) {
            System.out.println("Added or Removed track");
            String currentlyPlayingTrackId = getCurrentlyPlayingTrackId(spotifyPlayer.getSpotifyApi());
            if (slots.get("action").equals("add")) spotifyPlayer.saveTrackToLiked(currentlyPlayingTrackId);
            else spotifyPlayer.removeTrackFromLiked(currentlyPlayingTrackId);
        }
    }

    private void setVolume() throws IOException, ParseException, SpotifyWebApiException {
        System.out.println("Setting volume...");
        if (slots.containsKey("volume")) spotifyPlayer.setVolume(Integer.parseInt(slots.get("volume")));
        else spotifyPlayer.setVolume(100);
    }

    private void playByArtist() throws IOException, ParseException, SpotifyWebApiException {
        if (slots.containsKey("type") && slots.containsKey("artist")) {
            System.out.println("Playing music by artist");
            if (slots.get("type").equals("song")) spotifyPlayer.playTracksByArtist(slots.get("artist"));
            else spotifyPlayer.playAlbumByArtist(slots.get("artist"));
        }
    }

    private void playNewMusic() throws IOException, ParseException, SpotifyWebApiException {
        System.out.println("Playing new music");
        spotifyPlayer.playNewMusic();
    }

    private void playConcreteSongOrAlbum() throws IOException, ParseException, SpotifyWebApiException, CheetahException {
        String query = picovoiceRunner.getSearchQueryWithCheetah();
        if (slots.containsKey("item")) {
            if (slots.get("item").equals("song")) spotifyPlayer.playTrackByQuery(query);
            else spotifyPlayer.playAlbumByQuery(query);
            System.out.println("Playing " + query);
        }
    }
}
