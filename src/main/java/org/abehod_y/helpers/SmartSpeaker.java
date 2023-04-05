package org.abehod_y.helpers;

import org.abehod_y.picovoice.PicovoiceRunner;
import org.abehod_y.spotify.SpotifyPlayer;

public class SmartSpeaker {

    public static class Builder {
        private String picovoiceAccessKey;
        private String porcupineKeywordPath;
        private String rhinoContextPath;
        private SpotifyPlayer spotifyPlayer;

        public Builder() {}

        public Builder setPicovoiceAccessKey(String picovoiceAccessKey) {
            this.picovoiceAccessKey = picovoiceAccessKey;
            return this;
        }

        public Builder setPorcupineKeywordPath(String porcupineKeywordPath) {
            this.porcupineKeywordPath = porcupineKeywordPath;
            return this;
        }

        public Builder setRhinoContextPath(String rhinoContextPath) {
            this.rhinoContextPath = rhinoContextPath;
            return this;
        }

        public Builder setSpotifyPlayer(SpotifyPlayer spotifyPlayer) {
            this.spotifyPlayer = spotifyPlayer;
            return this;
        }

        public PicovoiceRunner build() {
            return new PicovoiceRunner(this.picovoiceAccessKey, this.porcupineKeywordPath, this.rhinoContextPath, this.spotifyPlayer);
        }
    }
}
