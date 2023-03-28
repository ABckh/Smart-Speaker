package org.abehod_y.picovoice;

import ai.picovoice.picovoice.*;
import ai.picovoice.picovoice.PicovoiceWakeWordCallback;
import org.abehod_y.spotify.SpotifyPlayer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.Properties;
import javax.sound.sampled.*;

public class PorcupineWakeWord {
    static public void run() throws PicovoiceException, IOException {
        InputStream input = new FileInputStream("src/main/resources/config.properties");
        Properties prop = new Properties();
        prop.load(input);

        final String accessKey = prop.getProperty("accessKey");
        final String keywordPath = prop.getProperty("keywordPath");
        final String contextPath = prop.getProperty("contextPath");

        PicovoiceWakeWordCallback wakeWordCallback = () -> {
            System.out.println("Wake word detected!");
            // let user know wake word was detected
        };

        PicovoiceInferenceCallback inferenceCallback = inference -> {
            if (inference.getIsUnderstood()) {
                final String intent = inference.getIntent();
                final Map<String, String> slots = inference.getSlots();
                // use intent and slots to trigger action

                String clientId = prop.getProperty("clientId");
                String clientSecret = prop.getProperty("clientSecret");
                String deviceId = prop.getProperty("deviceId");
                String accessToken = prop.getProperty("accessTokenSpotify");
                String refreshToken = prop.getProperty("refreshToken");

                SpotifyPlayer player = new SpotifyPlayer(clientId, clientSecret,
                        deviceId, accessToken, refreshToken);

                // TODO: Add integration with Spotify Player class
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
                        if (inference.getSlots().containsKey("volume")){
                            player.setVolume(Integer.parseInt(inference.getSlots().get("volume")));
                        } else {
                            player.setVolume(100);
                        }
                    }

                }

            }
        };

        Picovoice picovoice = new Picovoice.Builder()
                .setAccessKey(accessKey)
                .setKeywordPath(keywordPath)
                .setWakeWordCallback(wakeWordCallback)
                .setContextPath(contextPath)
                .setInferenceCallback(inferenceCallback)
                .build();

        AudioFormat format = new AudioFormat(16000f, 16,
                1, true, false);
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine micDataLine;
        try {
            micDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            micDataLine.open(format);
        } catch (LineUnavailableException e) {
            System.err.println("Failed to get a valid audio capture device.");
            return;
        }

        // start audio capture
        micDataLine.start();

        short[] picovoiceBuffer = new short[picovoice.getFrameLength()];
        ByteBuffer captureBuffer = ByteBuffer.allocate(picovoice.getFrameLength() * 2);
        captureBuffer.order(ByteOrder.LITTLE_ENDIAN);

        int numBytesRead;
        boolean recordingCancelled = false;
        while (!recordingCancelled) {

            // read a buffer of audio
            numBytesRead = micDataLine.read(captureBuffer.array(), 0, captureBuffer.capacity());

            // don't pass to Picovoice if we don't have a full buffer
            if (numBytesRead != picovoice.getFrameLength() * 2) {
                continue;
            }

            // copy into 16-bit buffer
            captureBuffer.asShortBuffer().get(picovoiceBuffer);

            // process with picovoice
            picovoice.process(picovoiceBuffer);
        }
    }

}
