package com.haleydu.cimoc.manager;

import android.util.Log;

import com.haleydu.cimoc.component.AppGetter;
import com.haleydu.cimoc.model.Tag;
import com.haleydu.cimoc.model.TagDao;
import com.haleydu.cimoc.model.Tag_;

import java.util.List;

import io.objectbox.query.QueryBuilder;
import rx.Observable;

/**
 * Created by Hiroshi on 2016/10/10.
 */

public class TagManager {
    private final static String TAG = "Cimoc-TagManager";
    public static final long TAG_CONTINUE = -101;
    public static final long TAG_FINISH = -100;

    private static volatile TagManager mInstance;

    private final TagDao mTagDao;

    private TagManager(AppGetter getter) {
        mTagDao = getter.getAppInstance().getDaoSession().getTagDao();
    }

    public static TagManager getInstance(AppGetter getter) {
        if (mInstance == null) {
            synchronized (TagManager.class) {
                if (mInstance == null) {
                    mInstance = new TagManager(getter);
                }
            }
        }
        return mInstance;
    }

    public List<Tag> list() {
        return mTagDao.queryBuilder().list();
    }

    public Observable<List<Tag>> listInRx() {
        Log.d(TAG, "[listInRx]");
        /*
        return mTagDao.queryBuilder()
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mTagDao.getBox()
                        .query()
                        .build()
                        .find()
        );
    }

    public Tag load(String title) {
        Log.d(TAG, "[load] title: " + title);
        return mTagDao.getBox()
                .query()
                .equal(Tag_.title, title, QueryBuilder.StringOrder.CASE_SENSITIVE)
                .build()
                .findFirst();
    }

    public void insert(Tag tag) {
        Log.d(TAG, "[insert] tag: " + tag);
        long id = mTagDao.insert(tag);
        tag.setId(id);
    }

    public void update(Tag tag) {
        Log.d(TAG, "[update] tag: " + tag);
        mTagDao.update(tag);
    }

    public void delete(Tag tag) {
        Log.d(TAG, "[delete] tag: " + tag);
        mTagDao.delete(tag);
    }

}
