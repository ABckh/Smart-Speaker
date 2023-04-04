package org.abehod_y.picovoice;

import ai.picovoice.cheetah.Cheetah;
import ai.picovoice.cheetah.CheetahTranscript;
import org.abehod_y.spotify.SpotifyPlayer;

import javax.sound.sampled.*;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CheetahRunner {
    private static final String libraryPath = Cheetah.LIBRARY_PATH;
    private static final String modelPath = Cheetah.MODEL_PATH;

    public static String getSearchQuery(String accessKey) {
        TargetDataLine micDataLine = PicovoiceBuilder.getMicDataLine();
        Cheetah cheetah = null;
        String searchQuery = "";
        try {
            cheetah = new Cheetah.Builder()
                    .setAccessKey(accessKey)
                    .setLibraryPath(libraryPath)
                    .setModelPath(modelPath)
//                    .setEndpointDuration(1f)
                    .build();


            micDataLine.start();

            System.out.println("Now listening...");

            // buffers for processing audio
            int frameLength = cheetah.getFrameLength();
            ByteBuffer captureBuffer = ByteBuffer.allocate(frameLength * 2);
            captureBuffer.order(ByteOrder.LITTLE_ENDIAN);
            short[] cheetahBuffer = new short[frameLength];

            int numBytesRead;
            long start = System.currentTimeMillis();
            long end = start + 7 * 1000;
            long totalBytesCaptured = 0;

            while (System.currentTimeMillis() < end) {

                // read a buffer of audio
                numBytesRead = micDataLine.read(captureBuffer.array(), 0, captureBuffer.capacity());
                totalBytesCaptured += numBytesRead;

                // don't pass to cheetah if we don't have a full buffer
                if (numBytesRead != frameLength * 2) {
                    continue;
                }

                // copy into 16-bit buffer
                captureBuffer.asShortBuffer().get(cheetahBuffer);

                // process with cheetah
                CheetahTranscript transcriptObj = cheetah.process(cheetahBuffer);
                searchQuery += transcriptObj.getTranscript();
                if (transcriptObj.getIsEndpoint()) {
                    CheetahTranscript endpointTranscriptObj = cheetah.flush();
                    searchQuery += endpointTranscriptObj.getTranscript();
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
        return searchQuery;
    }


//    private static TargetDataLine getAudioDevice(int deviceIndex,
//                                                 DataLine.Info dataLineInfo) throws LineUnavailableException {
//        if (deviceIndex >= 0) {
//            try {
//                Mixer.Info mixerInfo = AudioSystem.getMixerInfo()[deviceIndex];
//                Mixer mixer = AudioSystem.getMixer(mixerInfo);
//
//                if (mixer.isLineSupported(dataLineInfo)) {
//                    return (TargetDataLine) mixer.getLine(dataLineInfo);
//                }
//            } catch (Exception e) {
//                System.err.printf("No capture device found at index %s. Using default capture device.", deviceIndex);
//            }
//        }
//
//        // use default capture device if we couldn't get the one requested
//        return (TargetDataLine) AudioSystem.getLine(dataLineInfo);
//    }
}
