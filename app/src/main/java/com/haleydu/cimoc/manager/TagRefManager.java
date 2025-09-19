package com.haleydu.cimoc.manager;

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

    private static TagRefManager mInstance;

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
        return mRefDao.queryBuilder()
                .where(TagRefDao.Properties.Tid.equal(tid))
                .list();
    }

    public List<TagRef> listByComic(long cid) {
        return mRefDao.queryBuilder()
                .where(TagRefDao.Properties.Cid.equal(cid))
                .list();
    }

    public TagRef load(long tid, long cid) {
        return mRefDao.queryBuilder()
                .where(TagRefDao.Properties.Tid.equal(tid), TagRefDao.Properties.Cid.equal(cid))
                .unique();
    }

    public long insert(TagRef ref) {
        return mRefDao.insert(ref);
    }

    public void insert(Iterable<TagRef> entities) {
        mRefDao.insertInTx((Collection<TagRef>) entities);
    }

    public void insertInTx(Iterable<TagRef> entities) {
        mRefDao.insertInTx((Collection<TagRef>) entities);
    }

    public void deleteByTag(long tid) {
        /*
        mRefDao.queryBuilder()
                .where(TagRefDao.Properties.Tid.equal(tid))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
         */
        mRefDao.queryBuilder()
                .where(TagRefDao.Properties.Tid.equal(tid))
                .build()
                .remove();
    }

    public void deleteByComic(long cid) {
        /*
        mRefDao.queryBuilder()
                .where(TagRefDao.Properties.Cid.equal(cid))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
         */
        mRefDao.queryBuilder()
                .where(TagRefDao.Properties.Cid.equal(cid))
                .build()
                .remove();
    }

    public void delete(long tid, long cid) {
        /*
        mRefDao.queryBuilder()
                .where(TagRefDao.Properties.Tid.equal(tid), TagRefDao.Properties.Cid.equal(cid))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
         */
        mRefDao.queryBuilder()
                .where(TagRefDao.Properties.Tid.equal(tid), TagRefDao.Properties.Cid.equal(cid))
                .build()
                .remove();
    }

}
