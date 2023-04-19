package org.abehod_y.spotify.spotify_api.helpers;

import se.michaelthelin.spotify.model_objects.special.SearchResult;
import se.michaelthelin.spotify.model_objects.specification.*;

import java.util.Random;

public class ArraysHelpers {
    private ArraysHelpers() {}
    public static <T> void shuffle(T[] arr) {
        Random rand = new Random();
        for (int i = arr.length - 1; i >= 1; i--) {
            int j = rand.nextInt(i + 1);
            T temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static <T> T[] getItemsOrNull(Paging<T> paging) {
        return paging != null ? paging.getItems() : null;
    }

    public static TrackSimplified[] getTracksOrNull(Recommendations recommendations) {
        return recommendations != null ? recommendations.getTracks() : null;
    }

    public static Track[] getTracksOrNull(SearchResult searchResult) {
        return searchResult != null ? searchResult.getTracks().getItems() : null;
    }

    public static <T> T getFirstElement(T[] array) {
        return array.length == 0 ? null : array[0];
    }

    public static <T extends AlbumSimplified> T getFirstAlbumFromArray(T[] albums) {
        for (T album : albums) {
            if ("album".equals(album.getAlbumType().getType())) {
                return album;
            }
        }
        return null;
    }
}
