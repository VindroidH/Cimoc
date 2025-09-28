package com.haleydu.cimoc.manager;

import android.util.Log;

import com.haleydu.cimoc.component.AppGetter;
import com.haleydu.cimoc.model.Comic;
import com.haleydu.cimoc.model.ComicDao;
import com.haleydu.cimoc.model.Comic_;
import com.haleydu.cimoc.model.TagRef;
import com.haleydu.cimoc.model.TagRefDao;
import com.haleydu.cimoc.model.TagRef_;

import io.objectbox.Box;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import io.objectbox.query.QueryBuilder;
import rx.Observable;

/**
 * Created by Hiroshi on 2016/7/9.
 */
public class ComicManager {
    private static final String TAG = "Cimoc-ComicManager";

    private static volatile ComicManager mInstance;

    private final ComicDao mComicDao;
    private final TagRefDao mTagRefDao;

    private ComicManager(AppGetter getter) {
        mComicDao = getter.getAppInstance().getDaoSession().getComicDao();
        mTagRefDao = getter.getAppInstance().getDaoSession().getTagRefDao();
    }

    public static ComicManager getInstance(AppGetter getter) {
        if (mInstance == null) {
            synchronized (ComicManager.class) {
                if (mInstance == null) {
                    mInstance = new ComicManager(getter);
                }
            }
        }
        return mInstance;
    }

    public void runInTx(Runnable runnable) {
        mComicDao.getSession().runInTx(runnable);
    }

    public <T> T callInTx(Callable<T> callable) {
        return mComicDao.getSession().callInTxNoException(callable);
    }

    public List<Comic> listDownload() {
        Log.d(TAG, "[listDownload]");
        return mComicDao.getBox()
                .query()
                .notNull(Comic_.download)
                .build()
                .find();
    }

    public List<Comic> listLocal() {
        Log.d(TAG, "[listLocal]");
        return mComicDao.getBox()
                .query()
                .equal(Comic_.local, true)
                .build()
                .find();
    }

    public Observable<List<Comic>> listLocalInRx() {
        Log.d(TAG, "[listLocalInRx]");
        /*
        return mComicDao.queryBuilder()
                .where(Properties.Local.equal(true))
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mComicDao.getBox()
                        .query()
                        .equal(Comic_.local, true)
                        .build()
                        .find());
    }

    public Observable<List<Comic>> listFavoriteOrHistoryInRx() {
        Log.d(TAG, "[listFavoriteOrHistoryInRx]");
        /*
        return mComicDao.queryBuilder()
                .whereOr(Properties.Favorite.notNull(), Properties.History.notNull())
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mComicDao.getBox().query()
                        .notNull(Comic_.favorite)
                        .or()
                        .notNull(Comic_.history)
                        .build()
                        .find()
        );
    }

    public List<Comic> listFavorite() {
        Log.d(TAG, "[listFavorite]");
        return mComicDao.getBox()
                .query()
                .notNull(Comic_.favorite)
                .build()
                .find();
    }

    public Observable<List<Comic>> listFavoriteInRx() {
        Log.d(TAG, "[listFavoriteInRx]");
        /*
        return mComicDao.queryBuilder()
                .where(Properties.Favorite.notNull())
                .orderDesc(Properties.Highlight, Properties.Favorite)
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mComicDao.getBox()
                        .query()
                        .notNull(Comic_.favorite)
                        .orderDesc(Comic_.history)
                        .orderDesc(Comic_.favorite)
                        .build()
                        .find()
        );
    }

    public Observable<List<Comic>> listFinishInRx() {
        Log.d(TAG, "[listFinishInRx]");
        /*
        return mComicDao.queryBuilder()
                .where(Properties.Favorite.notNull(), Properties.Finish.equal(true))
                .orderDesc(Properties.Highlight, Properties.Favorite)
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mComicDao.getBox()
                        .query()
                        .notNull(Comic_.favorite)
                        .equal(Comic_.finish, true)
                        .orderDesc(Comic_.highlight)
                        .orderDesc(Comic_.favorite)
                        .build()
                        .find()
        );
    }

    public Observable<List<Comic>> listContinueInRx() {
        Log.d(TAG, "[listContinueInRx]");
        /*
        return mComicDao.queryBuilder()
                .where(Properties.Favorite.notNull(), Properties.Finish.notEqual(true))
                .orderDesc(Properties.Highlight, Properties.Favorite)
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mComicDao.getBox()
                        .query()
                        .notNull(Comic_.favorite)
                        .notEqual(Comic_.finish, true)
                        .orderDesc(Comic_.highlight)
                        .orderDesc(Comic_.favorite)
                        .build()
                        .find()
        );
    }

    public Observable<List<Comic>> listHistoryInRx() {
        Log.d(TAG, "[listHistoryInRx]");
        /*
        return mComicDao.queryBuilder()
                .where(Properties.History.notNull())
                .orderDesc(Properties.History)
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mComicDao.getBox()
                        .query()
                        .notNull(Comic_.history)
                        .orderDesc(Comic_.history)
                        .build()
                        .find()
        );
    }

    public Observable<List<Comic>> listDownloadInRx() {
        Log.d(TAG, "[listDownloadInRx]");
        /*
        return mComicDao.queryBuilder()
                .where(Properties.Download.notNull())
                .orderDesc(Properties.Download)
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mComicDao.getBox()
                        .query()
                        .notNull(Comic_.download)
                        .orderDesc(Comic_.download)
                        .build()
                        .find()
        );
    }

    public Observable<List<Comic>> listFavoriteByTag(long id) {
        Log.d(TAG, "[listFavoriteByTag] id: " + id);
        /*
        QueryBuilder<Comic> queryBuilder = mComicDao.queryBuilder();
        queryBuilder.join(TagRef.class, TagRefDao.Properties.Cid).where(TagRefDao.Properties.Tid.equal(id));
        return queryBuilder.orderDesc(Properties.Highlight, Properties.Favorite)
                .rx()
                .list();
         */

        return Observable.fromCallable(() -> {
            List<TagRef> tagRefs = mTagRefDao.getBox()
                    .query()
                    .equal(TagRef_.tid, id)
                    .build()
                    .find();
            long[] comicIds = new long[tagRefs.size()];
            for (int i = 0; i < tagRefs.size(); i++) {
                comicIds[i] = tagRefs.get(i).getCid();
            }
            return mComicDao.getBox().query()
                    .in(Comic_.id, comicIds)
                    .orderDesc(Comic_.highlight)
                    .orderDesc(Comic_.favorite)
                    .build()
                    .find();
        });

    }

    public Observable<List<Comic>> listFavoriteNotIn(Collection<Long> collections) {
        Log.d(TAG, "[listFavoriteNotIn] collections: " + collections);
        /*
        return mComicDao.queryBuilder()
                .where(Properties.Favorite.notNull(), Properties.Id.notIn(collections))
                .rx()
                .list();
         */
        long[] list = collections.stream()
                .mapToLong(Long::longValue)
                .toArray();
        return Observable.fromCallable(() ->
                mComicDao.getBox()
                        .query()
                        .notNull(Comic_.favorite)
                        .notIn(Comic_.id, list)
                        .build()
                        .find()
        );
    }

    public long countBySource(int type) {
        Log.d(TAG, "[countBySource] type: " + type);
        return mComicDao.getBox()
                .query()
                .equal(Comic_.source, type)
                .notNull(Comic_.favorite)
                .build()
                .count();
    }

    public Comic load(long id) {
        Log.d(TAG, "[load] id: " + id);
        return mComicDao.load(id);
    }

    public Comic load(int source, String cid) {
        Log.d(TAG, "[load] source: " + source + ", cid: " + cid);
        return mComicDao.getBox()
                .query()
                .equal(Comic_.source, source)
                .equal(Comic_.cid, cid, QueryBuilder.StringOrder.CASE_SENSITIVE)
                .build()
                .findUnique();
    }

    public Comic loadOrCreate(int source, String cid) {
        Log.d(TAG, "[loadOrCreate] source: " + source + ", cid: " + cid);
        Comic comic = load(source, cid);
        return comic == null ? new Comic(source, cid) : comic;
    }

    public Observable<Comic> loadLast() {
        Log.d(TAG, "[loadLast]");
        /*
        return mComicDao.queryBuilder()
                .where(Properties.History.notNull())
                .orderDesc(Properties.History)
                .limit(1)
                .rx()
                .unique();
         */

        return Observable.fromCallable(() ->
                mComicDao.getBox()
                        .query()
                        .notNull(Comic_.history)
                        .orderDesc(Comic_.history)
                        .build()
                        .findFirst()
        );
    }

    public void cancelHighlight() {
        Log.d(TAG, "[cancelHighlight]");
//        mComicDao.getDatabase().execSQL("UPDATE \"COMIC\" SET \"HIGHLIGHT\" = 0 WHERE \"HIGHLIGHT\" = 1");
        Box<Comic> comicBox = mComicDao.getBox();
        List<Comic> comics = comicBox.query()
                .equal(Comic_.highlight, true)
                .build()
                .find();

        for (Comic comic : comics) {
            comic.setHighlight(false);
        }
        comicBox.put(comics);
    }

    public void updateOrInsert(Comic comic) {
        Log.d(TAG, "[updateOrInsert] comic: " + comic);
        if (comic.getId() == null) {
            insert(comic);
        } else {
            update(comic);
        }
    }

    public void update(Comic comic) {
        Log.d(TAG, "[update] comic: " + comic);
        mComicDao.update(comic);
    }

    public void insertOrReplace(Comic comic) {
        Log.d(TAG, "[insertOrReplace] comic: " + comic);
        mComicDao.insertOrReplace(comic);
    }

    public void updateOrDelete(Comic comic) {
        Log.d(TAG, "[updateOrDelete] comic: " + comic);
        if (comic.getFavorite() == null && comic.getHistory() == null && comic.getDownload() == null) {
            mComicDao.delete(comic);
            comic.setId(null);
        } else {
            update(comic);
        }
    }

    public void deleteByKey(long key) {
        Log.d(TAG, "[deleteByKey] key: " + key);
        mComicDao.deleteByKey(key);
    }

    public void insert(Comic comic) {
        Log.d(TAG, "[insert] comic: " + comic);
        long id = mComicDao.insert(comic);
        comic.setId(id);
    }
}
