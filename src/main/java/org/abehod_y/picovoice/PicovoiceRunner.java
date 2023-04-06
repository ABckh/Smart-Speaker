package org.abehod_y.picovoice;

import ai.picovoice.cheetah.Cheetah;
import ai.picovoice.cheetah.CheetahException;
import ai.picovoice.cheetah.CheetahTranscript;
import ai.picovoice.picovoice.PicovoiceException;
import org.abehod_y.input_audio.Microphone;


public class PicovoiceRunner extends PicovoiceBuilder {

    private Microphone microphone;

    public PicovoiceRunner(String accessKey, String keywordPath, String contextPath) throws PicovoiceException, CheetahException {
        super(accessKey, keywordPath, contextPath);
    }

    public void run() throws PicovoiceException {
        int frameLength = this.getPicovoice().getFrameLength();
        microphone = new Microphone(frameLength);
        short[] picovoiceBuffer = microphone.getObjectBuffer();

        while (true) {
            microphone.readBuffer();
            this.getPicovoice().process(picovoiceBuffer);
        }
    }

    public String getSearchQueryWithCheetah() throws CheetahException {
        Cheetah cheetah = this.getCheetah();
        StringBuilder searchQuery = new StringBuilder();

        short[] cheetahBuffer = microphone.getObjectBuffer();
        long start = System.currentTimeMillis();
        long end = start + 7 * 1000;

        System.out.println("Now listening...");

        while (System.currentTimeMillis() < end) {
            microphone.readBuffer();

            // process with cheetah
            CheetahTranscript transcriptObj = cheetah.process(cheetahBuffer);
            searchQuery.append(transcriptObj.getTranscript());
            if (transcriptObj.getIsEndpoint()) {
                CheetahTranscript endpointTranscriptObj = cheetah.flush();
                searchQuery.append(endpointTranscriptObj.getTranscript());
            }
            System.out.flush();
        }
        System.out.println("Stopping...");
        if (cheetah != null) {
            cheetah.delete();
        }
        return searchQuery.toString();
    }
}