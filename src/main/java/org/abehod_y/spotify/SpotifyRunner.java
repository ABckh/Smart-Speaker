package org.abehod_y.spotify;

import ai.picovoice.cheetah.CheetahException;
import org.abehod_y.picovoice.PicovoiceRunner;
import org.abehod_y.spotify.spotify_api.SpotifyPlayer;

import java.util.Map;

import static org.abehod_y.spotify.spotify_api.helpers.SpotifyItemsIds.getCurrentlyPlayingTrackId;

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

    public void runTaskFromIntent(String intent) {
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

    private void playSomeMusic() {
        System.out.println("Playing music on spotify");
        if (slots.containsKey("genre")) {
            spotifyPlayer.playRecommendations(slots.get("genre"));
        } else if (slots.containsKey("item")) {
            if (slots.get("item").equals("music")) spotifyPlayer.playRandomMusic();
            else spotifyPlayer.playRandomAlbum();
        }
    }

    private void pausePlayback() {
        System.out.println("Stopping...");
        spotifyPlayer.pausePlaying();
    }

    private void resumePlayback() {
        System.out.println("Resuming...");
        spotifyPlayer.resumePlaying();
    }

    private void changeTrack() {
        if (slots.containsKey("pointer")) {
            System.out.println("Changing track...");
            if (slots.get("pointer").equals("next")) spotifyPlayer.nextTrack();
            else spotifyPlayer.previousTrack();
        }
    }

    private void addRemoveTrack()  {
        if (slots.containsKey("action")) {
            System.out.println("Added or Removed track");
            String currentlyPlayingTrackId = getCurrentlyPlayingTrackId(spotifyPlayer.getSpotifyApi());
            if (slots.get("action").equals("add")) spotifyPlayer.saveTrackToLiked(currentlyPlayingTrackId);
            else spotifyPlayer.removeTrackFromLiked(currentlyPlayingTrackId);
        }
    }

    private void setVolume()  {
        System.out.println("Setting volume...");
        if (slots.containsKey("volume")) spotifyPlayer.setVolume(Integer.parseInt(slots.get("volume")));
        else spotifyPlayer.setVolume(100);
    }

    private void playByArtist()  {
        if (slots.containsKey("type") && slots.containsKey("artist")) {
            System.out.println("Playing music by artist");
            if (slots.get("type").equals("song")) spotifyPlayer.playRandomTracksByArtist(slots.get("artist"));
            else spotifyPlayer.playRandomAlbumByArtist(slots.get("artist"));
        }
    }

    private void playNewMusic()  {
        System.out.println("Playing new music");
        spotifyPlayer.playNewMusic();
    }

    private void playConcreteSongOrAlbum() {
        try {
            String query = picovoiceRunner.getSearchQueryWithCheetah();
            if (slots.containsKey("item")) {
                if (slots.get("item").equals("song")) spotifyPlayer.playTrackByQuery(query);
                else spotifyPlayer.playAlbumByQuery(query);
                System.out.println("Playing " + query);
            }
        } catch (CheetahException e) {
            throw new RuntimeException(e);
        }
    }
}
