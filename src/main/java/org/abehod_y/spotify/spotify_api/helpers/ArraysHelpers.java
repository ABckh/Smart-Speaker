package org.abehod_y.spotify.spotify_api.helpers;

import se.michaelthelin.spotify.model_objects.AbstractModelObject;
import se.michaelthelin.spotify.model_objects.special.SearchResult;
import se.michaelthelin.spotify.model_objects.specification.*;

import java.util.Random;

public class ArraysHelpers {
    public static <T extends AbstractModelObject> void shuffle(T[] arr) {
        Random rand = new Random();
        for (int i = arr.length - 1; i >= 1; i--) {
            int j = rand.nextInt(i + 1);
            T temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static <T extends AbstractModelObject> T[] getItemsOrNull(Paging<T> paging) {
        if (paging == null) return null;
        return paging.getItems();
    }

    public static TrackSimplified[] getItemsOrNull(Recommendations recommendations) {
        if (recommendations == null) return null;
        return recommendations.getTracks();
    }

    public static Track[] getItemsOrNull(SearchResult searchResult) {
        if (searchResult == null) return null;
        return searchResult.getTracks().getItems();
    }

    public static <T> T getFirstElement(T[] array) {
        if (array.length == 0) return null;
        return array[0];
    }

    public static AlbumSimplified getFirstAlbumFromArray(AlbumSimplified[] albums) {
        for (AlbumSimplified album : albums) {
            if (album.getAlbumType().getType().equals("album")) return album;
        }
        return null;
    }
}
