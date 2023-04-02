package org.abehod_y.picovoice;

import ai.picovoice.cheetah.Cheetah;
import ai.picovoice.cheetah.CheetahTranscript;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CheetahRunner {
    private final String accessKey;
    private static final String libraryPath = Cheetah.LIBRARY_PATH;
    private static final String modelPath = Cheetah.MODEL_PATH;
    public CheetahRunner(String accessKey) {
        this.accessKey = accessKey;
    }

    public void run() {
        float endpointDuration = 1f;
        int audioDeviceIndex  = -1;
        // for file output
        File outputFile = null;
        ByteArrayOutputStream outputStream = null;
        long totalBytesCaptured = 0;
        AudioFormat format = new AudioFormat(16000f, 16, 1, true, false);

        // get audio capture device
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine micDataLine;
        try {
            micDataLine = getAudioDevice(audioDeviceIndex, dataLineInfo);
            micDataLine.open(format);
        } catch (LineUnavailableException e) {
            System.err.println("Failed to get a valid capture device. Use --show_audio_devices to " +
                    "show available capture devices and their indices");
            System.exit(1);
            return;
        }

        Cheetah cheetah = null;
        try {
            cheetah = new Cheetah.Builder()
                    .setAccessKey(accessKey)
                    .setLibraryPath(libraryPath)
                    .setModelPath(modelPath)
                    .setEndpointDuration(endpointDuration)
                    .build();


            micDataLine.start();

            System.out.println("Cheetah version : " + cheetah.getVersion());
            System.out.println("Now listening...");

            // buffers for processing audio
            int frameLength = cheetah.getFrameLength();
            ByteBuffer captureBuffer = ByteBuffer.allocate(frameLength * 2);
            captureBuffer.order(ByteOrder.LITTLE_ENDIAN);
            short[] cheetahBuffer = new short[frameLength];

            int numBytesRead;
            while (System.in.available() == 0) {

                // read a buffer of audio
                numBytesRead = micDataLine.read(captureBuffer.array(), 0, captureBuffer.capacity());
                totalBytesCaptured += numBytesRead;

                // write to output if we're recording
                if (outputStream != null) {
                    outputStream.write(captureBuffer.array(), 0, numBytesRead);
                }

                // don't pass to cheetah if we don't have a full buffer
                if (numBytesRead != frameLength * 2) {
                    continue;
                }

                // copy into 16-bit buffer
                captureBuffer.asShortBuffer().get(cheetahBuffer);

                // process with cheetah
                CheetahTranscript transcriptObj = cheetah.process(cheetahBuffer);
                System.out.print(transcriptObj.getTranscript());
                if (transcriptObj.getIsEndpoint()) {
                    CheetahTranscript endpointTranscriptObj = cheetah.flush();
                    System.out.println(endpointTranscriptObj.getTranscript());
                }
                System.out.flush();
            }
            System.out.println("Stopping...");
        } catch (Exception e) {
            System.err.println(e.toString());
        } finally {
            if (outputStream != null && outputFile != null) {

                // need to transfer to input stream to write
                ByteArrayInputStream writeArray = new ByteArrayInputStream(outputStream.toByteArray());
                AudioInputStream writeStream = new AudioInputStream(writeArray, format, totalBytesCaptured / format.getFrameSize());

                try {
                    AudioSystem.write(writeStream, AudioFileFormat.Type.WAVE, outputFile);
                } catch (IOException e) {
                    System.err.printf("Failed to write audio to '%s'.\n", outputFile.getPath());
                    e.printStackTrace();
                }
            }

            if (cheetah != null) {
                cheetah.delete();
            }
        }
    }

    private static TargetDataLine getDefaultCaptureDevice(DataLine.Info dataLineInfo) throws LineUnavailableException {
        if (!AudioSystem.isLineSupported(dataLineInfo)) {
            throw new LineUnavailableException("Default capture device does not support the audio " +
                    "format required by Picovoice (16kHz, 16-bit, linearly-encoded, single-channel PCM).");
        }

        return (TargetDataLine) AudioSystem.getLine(dataLineInfo);
    }

    private static TargetDataLine getAudioDevice(int deviceIndex,
                                                 DataLine.Info dataLineInfo) throws LineUnavailableException {
        if (deviceIndex >= 0) {
            try {
                Mixer.Info mixerInfo = AudioSystem.getMixerInfo()[deviceIndex];
                Mixer mixer = AudioSystem.getMixer(mixerInfo);

                if (mixer.isLineSupported(dataLineInfo)) {
                    return (TargetDataLine) mixer.getLine(dataLineInfo);
                } else {
                    System.err.printf("Audio capture device at index %s does not support the audio format required by " +
                            "Picovoice. Using default capture device.", deviceIndex);
                }
            } catch (Exception e) {
                System.err.printf("No capture device found at index %s. Using default capture device.", deviceIndex);
            }
        }

        // use default capture device if we couldn't get the one requested
        return getDefaultCaptureDevice(dataLineInfo);
    }
}
