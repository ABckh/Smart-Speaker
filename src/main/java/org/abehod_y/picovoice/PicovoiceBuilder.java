package org.abehod_y.picovoice;

import ai.picovoice.cheetah.Cheetah;
import ai.picovoice.cheetah.CheetahException;
import ai.picovoice.picovoice.Picovoice;
import ai.picovoice.picovoice.PicovoiceException;
import ai.picovoice.picovoice.PicovoiceInferenceCallback;
import org.abehod_y.spotify.SpotifyRunner;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.Map;


public class PicovoiceBuilder {
    private final Picovoice picovoice;
    private final Cheetah cheetah;
    private SpotifyRunner spotifyRunner;

    PicovoiceBuilder(String accessKey, String keywordPath, String contextPath) throws PicovoiceException, CheetahException {
        this.picovoice = new Picovoice.Builder()
                .setAccessKey(accessKey)
                .setKeywordPath(keywordPath)
                .setWakeWordCallback(() -> System.out.println("Wake word detected!"))
                .setContextPath(contextPath)
                .setInferenceCallback(getInferenceCallback())
                .build();
        this.cheetah = new Cheetah.Builder()
                .setAccessKey(accessKey)
                .build();
    }

    Picovoice getPicovoice() {
        return picovoice;
    }

    Cheetah getCheetah() {
        return cheetah;
    }

    public void setSpotifyRunner(SpotifyRunner spotifyRunner) {
        this.spotifyRunner = spotifyRunner;
    }

    private PicovoiceInferenceCallback getInferenceCallback() {
        return inference -> {
            if (inference.getIsUnderstood()) {
                final String intent = inference.getIntent();
                final Map<String, String> slots = inference.getSlots();
                try {
                    runTaskFromIntent(intent, slots);
                } catch (IOException | ParseException | SpotifyWebApiException | CheetahException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        };
    }

    private void runTaskFromIntent(String intent, Map<String, String> slots) throws IOException, ParseException, SpotifyWebApiException, CheetahException {
        assert spotifyRunner != null;
        spotifyRunner.setSlots(slots);
        switch (intent) {
            case "PlaySomeMusic" -> spotifyRunner.playSomeMusic();
            case "Pause" -> spotifyRunner.pausePlayback();
            case "Resume" -> spotifyRunner.resumePlayback();
            case "ChangeTrack" -> spotifyRunner.changeTrack();
            case "AddRemoveTrack" -> spotifyRunner.addRemoveTrack();
            case "SetVolume" -> spotifyRunner.setVolume();
            case "PlayByArtist" -> spotifyRunner.playByArtist();
            case "PlayNewMusic" -> spotifyRunner.playNewMusic();
            case "PlayConcreteSongOrAlbum" -> spotifyRunner.playConcreteSongOrAlbum();
        }
    }
}
