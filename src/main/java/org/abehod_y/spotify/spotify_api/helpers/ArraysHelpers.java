package org.abehod_y.spotify.spotify_api.helpers;

import se.michaelthelin.spotify.model_objects.AbstractModelObject;
import se.michaelthelin.spotify.model_objects.special.SearchResult;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Recommendations;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

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

}
