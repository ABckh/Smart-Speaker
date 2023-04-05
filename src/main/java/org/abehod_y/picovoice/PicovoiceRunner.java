package org.abehod_y.picovoice;

import ai.picovoice.picovoice.PicovoiceException;
import org.abehod_y.helpers.Microphone;
import org.abehod_y.spotify.SpotifyPlayer;


public class PicovoiceRunner extends PicovoiceBuilder {

    public PicovoiceRunner(String accessKey, String keywordPath, String contextPath, SpotifyPlayer spotifyPlayer) {
        super(accessKey, keywordPath, contextPath, spotifyPlayer);
    }

    public void run() {
        int frameLength = this.getPicovoice().getFrameLength();
        Microphone microphone = new Microphone(frameLength);
        short[] picovoiceBuffer = microphone.getObjectBuffer();
        this.setMicrophone(microphone);

        boolean recordingCancelled = false;
        while (!recordingCancelled) {
            microphone.readBuffer();
            try {
                this.getPicovoice().process(picovoiceBuffer);
            } catch (PicovoiceException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
