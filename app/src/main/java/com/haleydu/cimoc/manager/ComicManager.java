package com.haleydu.cimoc.manager;

import com.haleydu.cimoc.component.AppGetter;
import com.haleydu.cimoc.model.Comic;
import com.haleydu.cimoc.model.ComicDao;
import com.haleydu.cimoc.model.ComicDao.Properties;
import com.haleydu.cimoc.model.Comic_;
import com.haleydu.cimoc.model.TagRef;
import com.haleydu.cimoc.model.TagRefDao;
import com.haleydu.cimoc.model.TagRef_;

import org.greenrobot.daocompat.query.QueryBuilder;

import io.objectbox.Box;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Hiroshi on 2016/7/9.
 */
public class ComicManager {

    private static ComicManager mInstance;

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
        return mComicDao.queryBuilder()
                .where(Properties.Download.notNull())
                .list();
    }

    public List<Comic> listLocal() {
        return mComicDao.queryBuilder()
                .where(Properties.Local.equal(true))
                .list();
    }

    public Observable<List<Comic>> listLocalInRx() {
        /*
        return mComicDao.queryBuilder()
                .where(Properties.Local.equal(true))
                .rx()
                .list();
         */
        return Observable.fromCallable(() -> mComicDao.queryBuilder()
                .where(Properties.Local.equal(true))
                .list());
    }

    public Observable<List<Comic>> listFavoriteOrHistoryInRx() {
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
        return mComicDao.queryBuilder()
                .where(Properties.Favorite.notNull())
                .list();
    }

    public Observable<List<Comic>> listFavoriteInRx() {
        /*
        return mComicDao.queryBuilder()
                .where(Properties.Favorite.notNull())
                .orderDesc(Properties.Highlight, Properties.Favorite)
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mComicDao.queryBuilder()
                        .where(Properties.Favorite.notNull())
                        .orderDesc(Properties.Highlight, Properties.Favorite)
                        .list()
        );
    }

    public Observable<List<Comic>> listFinishInRx() {
        /*
        return mComicDao.queryBuilder()
                .where(Properties.Favorite.notNull(), Properties.Finish.equal(true))
                .orderDesc(Properties.Highlight, Properties.Favorite)
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mComicDao.queryBuilder()
                        .where(Properties.Favorite.notNull(), Properties.Finish.equal(true))
                        .orderDesc(Properties.Highlight, Properties.Favorite)
                        .list()
        );
    }

    public Observable<List<Comic>> listContinueInRx() {
        /*
        return mComicDao.queryBuilder()
                .where(Properties.Favorite.notNull(), Properties.Finish.notEqual(true))
                .orderDesc(Properties.Highlight, Properties.Favorite)
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mComicDao.queryBuilder()
                        .where(Properties.Favorite.notNull(), Properties.Finish.notEqual(true))
                        .orderDesc(Properties.Highlight, Properties.Favorite)
                        .list()
        );
    }

    public Observable<List<Comic>> listHistoryInRx() {
        /*
        return mComicDao.queryBuilder()
                .where(Properties.History.notNull())
                .orderDesc(Properties.History)
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mComicDao.queryBuilder()
                        .where(Properties.History.notNull())
                        .orderDesc(Properties.History)
                        .list()
        );
    }

    public Observable<List<Comic>> listDownloadInRx() {
        /*
        return mComicDao.queryBuilder()
                .where(Properties.Download.notNull())
                .orderDesc(Properties.Download)
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mComicDao.queryBuilder()
                        .where(Properties.Download.notNull())
                        .orderDesc(Properties.Download)
                        .list()
        );
    }

    public Observable<List<Comic>> listFavoriteByTag(long id) {
        /*
        QueryBuilder<Comic> queryBuilder = mComicDao.queryBuilder();
        queryBuilder.join(TagRef.class, TagRefDao.Properties.Cid).where(TagRefDao.Properties.Tid.equal(id));
        return queryBuilder.orderDesc(Properties.Highlight, Properties.Favorite)
                .rx()
                .list();
         */

        return Observable.fromCallable(() -> {
            List<TagRef> tagRefs = mTagRefDao.getBox().query()
                    .equal(TagRef_.tid, id)
                    .build()
                    .find();
            long[] comicIds = new long[tagRefs.size()];
            for (int i = 0; i < tagRefs.size(); i++) {
                comicIds[i] = tagRefs.get(i).getCid();
            }
            return mComicDao.getBox().query()
                    .in(Comic_.id, comicIds)  // 使用 IN 查询来模拟 JOIN
                    .orderDesc(Comic_.highlight)
                    .orderDesc(Comic_.favorite)
                    .build()
                    .find();
        });

    }

    public Observable<List<Comic>> listFavoriteNotIn(Collection<Long> collections) {
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
                mComicDao.queryBuilder()
                        .where(Properties.Favorite.notNull(), Properties.Id.notOneOf(list))
                        .list()
        );
    }

    public long countBySource(int type) {
        return mComicDao.queryBuilder()
                .where(Properties.Source.equal(type), Properties.Favorite.notNull())
                .count();
    }

    public Comic load(long id) {
        return mComicDao.load(id);
    }

    public Comic load(int source, String cid) {
        return mComicDao.queryBuilder()
                .where(Properties.Source.equal(source), Properties.Cid.equal(cid))
                .unique();
    }

    public Comic loadOrCreate(int source, String cid) {
        Comic comic = load(source, cid);
        return comic == null ? new Comic(source, cid) : comic;
    }

    public Observable<Comic> loadLast() {
        /*
        return mComicDao.queryBuilder()
                .where(Properties.History.notNull())
                .orderDesc(Properties.History)
                .limit(1)
                .rx()
                .unique();
         */
        return Observable.fromCallable(() ->
                mComicDao.queryBuilder()
                        .where(Properties.History.notNull())
                        .orderDesc(Properties.History)
                        .limit(1)
                        .unique()
        );
    }

    public void cancelHighlight() {
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
        if (comic.getId() == null) {
            insert(comic);
        } else {
            update(comic);
        }
    }

    public void update(Comic comic) {
        mComicDao.update(comic);
    }

    public void insertOrReplace(Comic comic) {
        mComicDao.insertOrReplace(comic);
    }

    public void updateOrDelete(Comic comic) {
        if (comic.getFavorite() == null && comic.getHistory() == null && comic.getDownload() == null) {
            mComicDao.delete(comic);
            comic.setId(null);
        } else {
            update(comic);
        }
    }

    public void deleteByKey(long key) {
        mComicDao.deleteByKey(key);
    }

    public void insert(Comic comic) {
        long id = mComicDao.insert(comic);
        comic.setId(id);
    }

}
