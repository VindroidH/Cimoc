package com.haleydu.cimoc.manager;

import android.util.Log;

import com.haleydu.cimoc.component.AppGetter;
import com.haleydu.cimoc.model.Chapter;
import com.haleydu.cimoc.model.ChapterDao;
import com.haleydu.cimoc.model.Chapter_;
import com.haleydu.cimoc.model.Comic;
import com.haleydu.cimoc.model.ComicDao;
import com.haleydu.cimoc.model.Comic_;

import java.util.List;
import java.util.concurrent.Callable;

import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Hiroshi on 2016/7/9.
 */
public class ChapterManager {
    private static final String TAG = "Cimoc-ChapterManager";

    private static volatile ChapterManager mInstance;

    private final ChapterDao mChapterDao;
    private final ComicDao mComicDao;

    private ChapterManager(AppGetter getter) {
        mChapterDao = getter.getAppInstance().getDaoSession().getChapterDao();
        mComicDao = getter.getAppInstance().getDaoSession().getComicDao();
    }

    public static ChapterManager getInstance(AppGetter getter) {
        if (mInstance == null) {
            synchronized (ChapterManager.class) {
                if (mInstance == null) {
                    mInstance = new ChapterManager(getter);
                }
            }
        }
        return mInstance;
    }

    public void runInTx(Runnable runnable) {
        mChapterDao.getSession().runInTx(runnable);
    }

    public <T> T callInTx(Callable<T> callable) {
        return mChapterDao.getSession().callInTxNoException(callable);
    }

    public Observable<List<Chapter>> getListChapter(Long sourceComic) {
        Log.d(TAG, "[getListChapter] sourceComic: " + sourceComic);
        /*
        return mChapterDao.queryBuilder()
                .where(Properties.SourceComic.equal(sourceComic))
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mChapterDao.getBox()
                        .query()
                        .equal(Chapter_.sourceComic, sourceComic)
                        .build()
                        .find()
        );
    }

    public List<Chapter> getChapter(String path, String title) {
        Log.d(TAG, "[getChapter] path: " + path + ", title: " + title);
        return mChapterDao.getBox()
                .query()
                .equal(Chapter_.path, path, QueryBuilder.StringOrder.CASE_SENSITIVE)
                .equal(Chapter_.title, title, QueryBuilder.StringOrder.CASE_SENSITIVE)
                .build()
                .find();
    }


    public Chapter load(long id) {
        return mChapterDao.load(id);
    }


    public void cancelHighlight() {
        Log.d(TAG, "[cancelHighlight]");
//        mChapterDao.getDatabase().execSQL("UPDATE \"COMIC\" SET \"HIGHLIGHT\" = 0 WHERE \"HIGHLIGHT\" = 1");
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

    public void updateOrInsert(List<Chapter> chapterList) {
        Log.d(TAG, "[updateOrInsert] chapterList: " + chapterList);
        for (Chapter chapter : chapterList) {
            if (chapter.getId() == null) {
                insert(chapter);
            } else {
                update(chapter);
            }
        }
    }

    public void insertOrReplace(List<Chapter> chapterList) {
        Log.d(TAG, "[insertOrReplace] chapterList: " + chapterList);
        for (Chapter chapter : chapterList) {
            if (chapter.getId() != null) {
                mChapterDao.insertOrReplace(chapter);
            }
        }
    }

    public void update(Chapter chapter) {
        Log.d(TAG, "[update] chapter: " + chapter);
        if (chapter.getId() != null) {
            mChapterDao.update(chapter);
        }
    }

    public void deleteByKey(long key) {
        Log.d(TAG, "[deleteByKey] key: " + key);
        mChapterDao.deleteByKey(key);
    }

    public void insert(Chapter chapter) {
        Log.d(TAG, "[insert] chapter: " + chapter);
        long id = mChapterDao.insert(chapter);
        chapter.setId(id);
    }
}
