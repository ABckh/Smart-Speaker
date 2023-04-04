package org.abehod_y.picovoice;

import ai.picovoice.picovoice.Picovoice;
import ai.picovoice.picovoice.PicovoiceException;
import ai.picovoice.picovoice.PicovoiceInferenceCallback;
import ai.picovoice.picovoice.PicovoiceWakeWordCallback;
import org.abehod_y.spotify.SpotifyPlayer;

import javax.sound.sampled.*;
import java.util.Map;

public class PicovoiceBuilder {
    private Picovoice picovoice;
    private String accessKey;

    PicovoiceBuilder(String accessKey, String keywordPath, String contextPath, SpotifyPlayer spotifyPlayer) {
        try {
            this.accessKey = accessKey;
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
                    case "PlaySomeMusic" -> {
                        System.out.println("Playing music on spotify");
                        if (slots.containsKey("genre")) {
                            player.playRecommendations(slots.get("genre"));
                        } else if (slots.containsKey("item")) {
                            if (slots.get("item").equals("music")) player.playSomeMusic();
                            else player.playSomeAlbum();
                        }
                    }
                    case "Pause" -> {
                        System.out.println("Stopping...");
                        player.pausePlaying();
                    }
                    case "Resume" -> {
                        System.out.println("Resuming...");
                        player.resumePlaying();
                    }
                    case "ChangeTrack" -> {
                        if (slots.containsKey("pointer")) {
                            System.out.println("Changing track...");
                            if (slots.get("pointer").equals("next")) player.nextTrack();
                            else player.previousTrack();
                        }
                    }
                    case "AddRemoveTrack" -> {
                        if (slots.containsKey("action")) {
                            System.out.println("Added or Removed track");
                            if (slots.get("action").equals("add")) player.saveTrackToLiked();
                            else player.removeTrackFromLiked();
                        }
                    }
                    case "SetVolume" -> {
                        System.out.println("Setting volume...");
                        if (slots.containsKey("volume")) player.setVolume(Integer.parseInt(slots.get("volume")));
                        else player.setVolume(100);
                    }
                    case "PlayByArtist" -> {
                        if (slots.containsKey("type") && slots.containsKey("artist")) {
                            System.out.println("Playing music by artist");
                            if (slots.get("type").equals("song")) player.playTracksByArtist(slots.get("artist"));
                            else player.playAlbumByArtist(slots.get("artist"));
                        }
                    }
                    case "PlayNewMusic" -> {
                        System.out.println("Playing new music");
                        player.playNewMusic();
                    }
                    case "PlayConcreteSongOrAlbum" -> {
                        if (slots.containsKey("item")) {
                            String query = CheetahRunner.getSearchQuery(accessKey);
                            if (slots.get("item").equals("song")) {
                                player.playTrackByQuery(query);
                            } else {
                                player.playAlbumByQuery(query);
                            }
                            System.out.println("Playing " + query);
                        }
                    }
                }
            }
        };
    }
    static TargetDataLine getMicDataLine() {
        AudioFormat format = new AudioFormat(16000f, 16,
                1, true, false);
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine micDataLine = null;
        try {
            micDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            micDataLine.open(format);
        } catch (LineUnavailableException e) {
            System.err.println("Failed to get a valid audio capture device.");
        }
        return micDataLine;
    }
    public Picovoice getPicovoice() {
        return picovoice;
    }
}
