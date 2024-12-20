package com.haleydu.cimoc.utils;

import com.haleydu.cimoc.model.Comic;

import java.util.List;

import androidx.collection.LongSparseArray;

/**
 * Created by Hiroshi on 2017/3/24.
 */

public class ComicUtils {

    public static LongSparseArray<Comic> buildComicMap(List<Comic> list) {
        LongSparseArray<Comic> array = new LongSparseArray<>();
        for (Comic comic : list) {
            array.put(comic.getId(), comic);
        }
        return array;
    }

}
