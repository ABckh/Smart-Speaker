package org.abehod_y.picovoice;

import ai.picovoice.cheetah.Cheetah;
import ai.picovoice.cheetah.CheetahException;
import ai.picovoice.picovoice.Picovoice;
import ai.picovoice.picovoice.PicovoiceException;
import ai.picovoice.picovoice.PicovoiceInferenceCallback;
import org.abehod_y.spotify.SpotifyRunner;
import java.awt.Toolkit;


import java.util.Map;


public class PicovoiceBuilder {
    private final Picovoice picovoice;
    private final Cheetah cheetah;
    private SpotifyRunner spotifyRunner;

    PicovoiceBuilder(String accessKey, String keywordPath, String contextPath) throws PicovoiceException, CheetahException {
        this.picovoice = new Picovoice.Builder()
                .setAccessKey(accessKey)
                .setKeywordPath(keywordPath)
//                .setWakeWordCallback(() -> System.out.println("Wake word detected!"))
                .setWakeWordCallback(() -> Toolkit.getDefaultToolkit().beep())
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
                    spotifyRunner.setSlots(slots);
                    spotifyRunner.runTaskFromIntent(intent);
            }
        };
    }
}
