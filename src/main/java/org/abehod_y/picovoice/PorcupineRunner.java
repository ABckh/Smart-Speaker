package org.abehod_y.picovoice;

import ai.picovoice.picovoice.PicovoiceException;
import org.abehod_y.spotify.SpotifyPlayer;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PorcupineRunner extends PicovoiceBuilder {

    public PorcupineRunner(String accessKey, String keywordPath, String contextPath, SpotifyPlayer spotifyPlayer) {
        super(accessKey, keywordPath, contextPath, spotifyPlayer);
    }

    public void run() {
        TargetDataLine micDataLine = getMicDataLine();

        // start audio capture
        micDataLine.start();

        short[] picovoiceBuffer = new short[this.getPicovoice().getFrameLength()];
        ByteBuffer captureBuffer = ByteBuffer.allocate(this.getPicovoice().getFrameLength() * 2);
        captureBuffer.order(ByteOrder.LITTLE_ENDIAN);

        int numBytesRead;
        boolean recordingCancelled = false;
        while (!recordingCancelled) {

            // read a buffer of audio
            numBytesRead = micDataLine.read(captureBuffer.array(), 0, captureBuffer.capacity());

            // don't pass to Picovoice if we don't have a full buffer
            if (numBytesRead != this.getPicovoice().getFrameLength() * 2) {
                continue;
            }

            // copy into 16-bit buffer
            captureBuffer.asShortBuffer().get(picovoiceBuffer);

            // process with picovoice
            try {
                this.getPicovoice().process(picovoiceBuffer);
            } catch (PicovoiceException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private TargetDataLine getMicDataLine() {
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
}
