package org.abehod_y.spotify.spotify_api.helpers;

import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.AbstractModelObject;
import se.michaelthelin.spotify.requests.AbstractRequest;
import se.michaelthelin.spotify.requests.data.AbstractDataRequest;

import java.io.IOException;

public class Requests {
    public static void executeRequest(AbstractRequest<?> request) {
        try {
            request.execute();
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static <T extends AbstractModelObject> T executeRequestWithReturn(AbstractDataRequest<T> request) {
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
