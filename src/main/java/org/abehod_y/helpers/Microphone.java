package org.abehod_y.helpers;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Microphone {
    private final int frameLength;
    private final short[] objectBuffer;
    private final ByteBuffer captureBuffer;
    private final TargetDataLine micDataLine;

    public Microphone(int frameLength) {
        this.frameLength = frameLength;
        this.objectBuffer = new short[frameLength];
        this.micDataLine = createMicDataLine();
        this.captureBuffer = createCaptureBuffer();
    }

    public short[] getObjectBuffer() {
        return objectBuffer;
    }

    private TargetDataLine createMicDataLine() {
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
        assert micDataLine != null;
        micDataLine.start();
        return micDataLine;
    }

    private ByteBuffer createCaptureBuffer() {
        ByteBuffer captureBuffer = ByteBuffer.allocate(frameLength * 2);
        captureBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return captureBuffer;
    }

    public void readBuffer() {
        // read a buffer of audio
        int numBytesRead = micDataLine.read(captureBuffer.array(), 0, captureBuffer.capacity());

        // don't pass to cheetah if we don't have a full buffer
        if (numBytesRead != frameLength * 2) {
            return;
        }

        // copy into 16-bit buffer
        captureBuffer.asShortBuffer().get(objectBuffer);
    }
}
