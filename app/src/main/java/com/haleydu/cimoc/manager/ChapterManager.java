package com.haleydu.cimoc.manager;

import com.haleydu.cimoc.component.AppGetter;
import com.haleydu.cimoc.model.Chapter;
import com.haleydu.cimoc.model.ChapterDao;
import com.haleydu.cimoc.model.ChapterDao.Properties;
import com.haleydu.cimoc.model.Chapter_;
import com.haleydu.cimoc.model.Comic;
import com.haleydu.cimoc.model.ComicDao;
import com.haleydu.cimoc.model.Comic_;

import java.util.List;
import java.util.concurrent.Callable;

import io.objectbox.Box;
import rx.Observable;
import rx.schedulers.Schedulers;


/**
 * Created by Hiroshi on 2016/7/9.
 */
public class ChapterManager {

    private static ChapterManager mInstance;

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
        /*
        return mChapterDao.queryBuilder()
                .where(Properties.SourceComic.equal(sourceComic))
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mChapterDao.queryBuilder()
                        .where(Properties.SourceComic.equal(sourceComic))
                        .list());
    }

    public List<Chapter> getChapter(String path, String title) {
        return mChapterDao.queryBuilder()
                .where(ChapterDao.Properties.Path.equal(path), ChapterDao.Properties.Title.equal(title))
                .list();
    }


    public Chapter load(long id) {
        return mChapterDao.load(id);
    }


    public void cancelHighlight() {
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
        for (Chapter chapter : chapterList) {
            if (chapter.getId() == null) {
                insert(chapter);
            } else {
                update(chapter);
            }
        }
    }

    public void insertOrReplace(List<Chapter> chapterList) {
        for (Chapter chapter : chapterList) {
            if (chapter.getId() != null) {
                mChapterDao.insertOrReplace(chapter);
            }
        }
    }

    public void update(Chapter chapter) {
        if (chapter.getId() != null) {
            mChapterDao.update(chapter);
        }
    }

    public void deleteByKey(long key) {
        mChapterDao.deleteByKey(key);
    }

    public void insert(Chapter chapter) {
        long id = mChapterDao.insert(chapter);
        chapter.setId(id);
    }

}
