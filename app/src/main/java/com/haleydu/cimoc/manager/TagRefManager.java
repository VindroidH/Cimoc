package com.haleydu.cimoc.manager;

import android.util.Log;

import com.haleydu.cimoc.component.AppGetter;
import com.haleydu.cimoc.model.TagRef;
import com.haleydu.cimoc.model.TagRefDao;
import com.haleydu.cimoc.model.TagRef_;

import java.util.Collection;
import java.util.List;

import rx.Observable;

/**
 * Created by Hiroshi on 2017/1/16.
 */

public class TagRefManager {
    private final static String TAG = "Cimoc-TagRefManager";
    private static volatile TagRefManager mInstance;

    private final TagRefDao mRefDao;

    private TagRefManager(AppGetter getter) {
        mRefDao = getter.getAppInstance().getDaoSession().getTagRefDao();
    }

    public static TagRefManager getInstance(AppGetter getter) {
        if (mInstance == null) {
            synchronized (TagRefManager.class) {
                if (mInstance == null) {
                    mInstance = new TagRefManager(getter);
                }
            }
        }
        return mInstance;
    }

    public Observable<Void> runInRx(Runnable runnable) {
        return mRefDao.getSession().rxTx().run(runnable);
    }

    public void runInTx(Runnable runnable) {
        mRefDao.getSession().runInTx(runnable);
    }

    public List<TagRef> listByTag(long tid) {
        Log.d(TAG, "[listByTag] tid: " + tid);
        return mRefDao.getBox()
                .query()
                .equal(TagRef_.tid, tid)
                .build()
                .find();
    }

    public List<TagRef> listByComic(long cid) {
        Log.d(TAG, "[listByComic] cid: " + cid);
        return mRefDao.getBox()
                .query()
                .equal(TagRef_.cid, cid)
                .build()
                .find();
    }

    public TagRef load(long tid, long cid) {
        Log.d(TAG, "[load] tid: " + tid + ", cid: " + cid);
        return mRefDao.getBox()
                .query()
                .equal(TagRef_.tid, tid)
                .equal(TagRef_.cid, cid)
                .build()
                .findUnique();
    }

    public long insert(TagRef ref) {
        Log.d(TAG, "[insert] ref: " + ref);
        return mRefDao.insert(ref);
    }

    public void insert(Iterable<TagRef> entities) {
        Log.d(TAG, "[insert]");
        mRefDao.insertInTx((Collection<TagRef>) entities);
    }

    public void insertInTx(Iterable<TagRef> entities) {
        Log.d(TAG, "[insertInTx]");
        mRefDao.insertInTx((Collection<TagRef>) entities);
    }

    public void deleteByTag(long tid) {
        Log.d(TAG, "[deleteByTag] tid: " + tid);
        /*
        mRefDao.queryBuilder()
                .where(TagRefDao.Properties.Tid.equal(tid))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
         */
        mRefDao.getBox()
                .query()
                .equal(TagRef_.tid, tid)
                .build()
                .remove();
    }

    public void deleteByComic(long cid) {
        Log.d(TAG, "[deleteByComic] cid: " + cid);
        /*
        mRefDao.queryBuilder()
                .where(TagRefDao.Properties.Cid.equal(cid))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
         */
        mRefDao.getBox()
                .query()
                .equal(TagRef_.cid, cid)
                .build()
                .remove();
    }

    public void delete(long tid, long cid) {
        Log.d(TAG, "[delete] tid: " + tid + ", cid: " + cid);
        /*
        mRefDao.queryBuilder()
                .where(TagRefDao.Properties.Tid.equal(tid), TagRefDao.Properties.Cid.equal(cid))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
         */
        mRefDao.getBox()
                .query()
                .equal(TagRef_.tid, tid)
                .equal(TagRef_.cid, cid)
                .build()
                .remove();
    }
}
