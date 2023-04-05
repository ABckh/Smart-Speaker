package org.abehod_y.picovoice;

import ai.picovoice.cheetah.Cheetah;
import ai.picovoice.cheetah.CheetahTranscript;
import ai.picovoice.picovoice.Picovoice;
import ai.picovoice.picovoice.PicovoiceException;
import ai.picovoice.picovoice.PicovoiceInferenceCallback;
import org.abehod_y.helpers.Microphone;
import org.abehod_y.spotify.SpotifyPlayer;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.Map;


public class PicovoiceBuilder {
    private final Picovoice picovoice;
    private final String accessKey;
    private final SpotifyPlayer spotifyPlayer;
    private Microphone microphone;

    PicovoiceBuilder(String accessKey, String keywordPath, String contextPath, SpotifyPlayer spotifyPlayer) throws PicovoiceException {
        this.accessKey = accessKey;
        this.spotifyPlayer = spotifyPlayer;
        this.picovoice = new Picovoice.Builder()
                .setAccessKey(accessKey)
                .setKeywordPath(keywordPath)
                .setWakeWordCallback(() -> System.out.println("Wake word detected!"))
                .setContextPath(contextPath)
                .setInferenceCallback(getInferenceCallback())
                .build();
    }

    public Picovoice getPicovoice() {
        return picovoice;
    }

    public void setMicrophone(Microphone microphone) {
        this.microphone = microphone;
    }

    private PicovoiceInferenceCallback getInferenceCallback() {
        return inference -> {
            if (inference.getIsUnderstood()) {
                final String intent = inference.getIntent();
                final Map<String, String> slots = inference.getSlots();
                try {
                    runTaskFromIntent(intent, slots);
                } catch (IOException | ParseException | SpotifyWebApiException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        };
    }

    private String getSearchQuery() {
        Cheetah cheetah = null;
        StringBuilder searchQuery = new StringBuilder();
        try {
            cheetah = new Cheetah.Builder()
                    .setAccessKey(accessKey)
                    .build();

            short[] cheetahBuffer = microphone.getObjectBuffer();
            long start = System.currentTimeMillis();
            long end = start + 7 * 1000;

            System.out.println("Now listening...");

            while (System.currentTimeMillis() < end) {

                microphone.readBuffer();

                // process with cheetah
                CheetahTranscript transcriptObj = cheetah.process(cheetahBuffer);
                searchQuery.append(transcriptObj.getTranscript());
                if (transcriptObj.getIsEndpoint()) {
                    CheetahTranscript endpointTranscriptObj = cheetah.flush();
                    searchQuery.append(endpointTranscriptObj.getTranscript());
                }
                System.out.flush();
            }
            System.out.println("Stopping...");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            if (cheetah != null) {
                cheetah.delete();
            }
        }
        return searchQuery.toString();
    }

    private void runTaskFromIntent(String intent, Map<String, String> slots) throws IOException, ParseException, SpotifyWebApiException {
        switch (intent) {
            case "PlaySomeMusic" -> {
                System.out.println("Playing music on spotify");
                if (slots.containsKey("genre")) {
                    spotifyPlayer.playRecommendations(slots.get("genre"));
                } else if (slots.containsKey("item")) {
                    if (slots.get("item").equals("music")) spotifyPlayer.playSomeMusic();
                    else spotifyPlayer.playSomeAlbum();
                }
            }
            case "Pause" -> {
                System.out.println("Stopping...");
                spotifyPlayer.pausePlaying();
            }
            case "Resume" -> {
                System.out.println("Resuming...");
                spotifyPlayer.resumePlaying();
            }
            case "ChangeTrack" -> {
                if (slots.containsKey("pointer")) {
                    System.out.println("Changing track...");
                    if (slots.get("pointer").equals("next")) spotifyPlayer.nextTrack();
                    else spotifyPlayer.previousTrack();
                }
            }
            case "AddRemoveTrack" -> {
                if (slots.containsKey("action")) {
                    System.out.println("Added or Removed track");
                    String currentlyPlayingTrackId = spotifyPlayer.getCurrentlyPlayingTrackId();
                    if (slots.get("action").equals("add")) spotifyPlayer.saveTrackToLiked(currentlyPlayingTrackId);
                    else spotifyPlayer.removeTrackFromLiked(currentlyPlayingTrackId);
                }
            }
            case "SetVolume" -> {
                System.out.println("Setting volume...");
                if (slots.containsKey("volume")) spotifyPlayer.setVolume(Integer.parseInt(slots.get("volume")));
                else spotifyPlayer.setVolume(100);
            }
            case "PlayByArtist" -> {
                if (slots.containsKey("type") && slots.containsKey("artist")) {
                    System.out.println("Playing music by artist");
                    if (slots.get("type").equals("song")) spotifyPlayer.playTracksByArtist(slots.get("artist"));
                    else spotifyPlayer.playAlbumByArtist(slots.get("artist"));
                }
            }
            case "PlayNewMusic" -> {
                System.out.println("Playing new music");
                spotifyPlayer.playNewMusic();
            }
            case "PlayConcreteSongOrAlbum" -> {
                if (slots.containsKey("item")) {
                    String query = getSearchQuery();
                    if (slots.get("item").equals("song")) spotifyPlayer.playTrackByQuery(query);
                    else spotifyPlayer.playAlbumByQuery(query);
                    System.out.println("Playing " + query);
                }
            }
        }
    }
}
