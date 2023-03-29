package org.abehod_y.picovoice;

import ai.picovoice.picovoice.Picovoice;
import ai.picovoice.picovoice.PicovoiceException;
import ai.picovoice.picovoice.PicovoiceInferenceCallback;
import ai.picovoice.picovoice.PicovoiceWakeWordCallback;
import org.abehod_y.spotify.SpotifyPlayer;

import java.util.Map;

public class PicovoiceBuilder {
    private Picovoice picovoice;

    public PicovoiceBuilder(String accessKey, String keywordPath, String contextPath, SpotifyPlayer spotifyPlayer) {
        try {
            this.picovoice = new Picovoice.Builder()
                    .setAccessKey(accessKey)
                    .setKeywordPath(keywordPath)
                    .setWakeWordCallback(getWakeWordCallback())
                    .setContextPath(contextPath)
                    .setInferenceCallback(getInferenceCallback(spotifyPlayer))
                    .build();
        } catch (PicovoiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private PicovoiceWakeWordCallback getWakeWordCallback() {
        return () -> {
            System.out.println("Wake word detected!");
        };
    }

    private PicovoiceInferenceCallback getInferenceCallback(SpotifyPlayer player) {
        return inference -> {
            if (inference.getIsUnderstood()) {
                final String intent = inference.getIntent();
                final Map<String, String> slots = inference.getSlots();
                switch (intent) {
                    case "PlayMusic" -> {
                        System.out.println("Playing music on spotify");
                        player.playSomething();
                    }
                    case "Pause" -> {
                        System.out.println("Stopping...");
                        player.pausePlaying();
                    }
                    case "Resume" -> {
                        System.out.println("Resuming...");
                        player.resumePlaying();
                    }
                    case "Next" -> {
                        System.out.println("Playing next track...");
                        player.nextTrack();
                    }
                    case "Previous" -> {
                        System.out.println("Playing previous track...");
                        player.previousTrack();
                    }
                    case "AddTrack" -> {
                        System.out.println("Added track to liked");
                        player.saveTrackToLiked();
                    }
                    case "RemoveTrack" -> {
                        System.out.println("Removed track from library");
                        player.removeTrackFromLiked();
                    }
                    case "SetVolume" -> {
                        System.out.println("Setting volume...");
                        if (inference.getSlots().containsKey("volume")) {
                            player.setVolume(Integer.parseInt(inference.getSlots().get("volume")));
                        } else {
                            player.setVolume(100);
                        }
                    }
                }
            }
        };
    }

    public Picovoice getPicovoice() {
        return picovoice;
    }
}
