package org.abehod_y;

import ai.picovoice.cheetah.CheetahException;
import ai.picovoice.picovoice.PicovoiceException;
import org.abehod_y.picovoice.PicovoiceRunner;
import org.abehod_y.spotify.SpotifyRunner;

public class SmartSpeaker {

    public static class Builder {
        private String picovoiceAccessKey;
        private String porcupineKeywordPath;
        private String rhinoContextPath;
        private String spotifyClientId;
        private String spotifyClientSecret;
        private String spotifyDeviceId;
        private String spotifyRefreshToken;
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

        public Builder setSpotifyClientId(String spotifyClientId) {
            this.spotifyClientId = spotifyClientId;
            return this;
        }

        public Builder setSpotifyClientSecret(String spotifyClientSecret) {
            this.spotifyClientSecret = spotifyClientSecret;
            return this;
        }

        public Builder setSpotifyDeviceId(String spotifyDeviceId) {
            this.spotifyDeviceId = spotifyDeviceId;
            return this;
        }

        public Builder setSpotifyRefreshToken(String spotifyRefreshToken) {
            this.spotifyRefreshToken = spotifyRefreshToken;
            return this;
        }

        public PicovoiceRunner build() throws PicovoiceException, CheetahException {
            PicovoiceRunner picovoiceRunner = new PicovoiceRunner(this.picovoiceAccessKey, this.porcupineKeywordPath, this.rhinoContextPath);
            SpotifyRunner spotifyRunner = new SpotifyRunner(this.spotifyClientId, this.spotifyClientSecret, this.spotifyRefreshToken, this.spotifyDeviceId, picovoiceRunner);
            picovoiceRunner.setSpotifyRunner(spotifyRunner);
            return picovoiceRunner;
        }
    }
}
