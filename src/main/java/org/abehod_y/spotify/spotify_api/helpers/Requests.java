package org.abehod_y.spotify.spotify_api.helpers;

import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.AbstractModelObject;
import se.michaelthelin.spotify.requests.AbstractRequest;
import se.michaelthelin.spotify.requests.data.AbstractDataRequest;

import java.io.IOException;

public class Requests {
    private Requests() {}

    public static void executeRequest(AbstractRequest<?> request) {
        try {
            request.execute();
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void stopThread(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends AbstractModelObject> T executeRequestWithDataReturn(AbstractDataRequest<T> request) {
        try {
            return request.execute();
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public static <T extends AbstractModelObject> T[] executeRequestWithArrayReturn(AbstractDataRequest<T[]> request) {
        try {
            return request.execute();
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}
