package com.haleydu.cimoc.manager;

import android.util.Log;

import com.haleydu.cimoc.component.AppGetter;
import com.haleydu.cimoc.model.ImageUrl;
import com.haleydu.cimoc.model.ImageUrlDao;
import com.haleydu.cimoc.model.ImageUrl_;

import java.util.List;

import rx.Observable;

/**
 * Created by HaleyDu on 2020/8/27.
 */
public class ImageUrlManager {
    private final static String TAG = "Cimoc-ImageUrlManager";

    private static volatile ImageUrlManager mInstance;

    private final ImageUrlDao mImageUrlDao;

    private ImageUrlManager(AppGetter getter) {
        mImageUrlDao = getter.getAppInstance().getDaoSession().getImageUrlDao();
    }

    public static ImageUrlManager getInstance(AppGetter getter) {
        if (mInstance == null) {
            synchronized (ImageUrlManager.class) {
                if (mInstance == null) {
                    mInstance = new ImageUrlManager(getter);
                }
            }
        }
        return mInstance;
    }

    public void runInTx(Runnable runnable) {
        mImageUrlDao.getSession().runInTx(runnable);
    }

    public Observable<List<ImageUrl>> getListImageUrlRX(Long comicChapter) {
        Log.d(TAG, "[getListImageUrlRX] comicChapter: " + comicChapter);
        /*
        return mImageUrlDao.queryBuilder()
                .where(ImageUrlDao.Properties.ComicChapter.equal(comicChapter))
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mImageUrlDao.getBox()
                        .query()
                        .equal(ImageUrl_.comicChapter, comicChapter)
                        .build()
                        .find()
        );
    }

    public List<ImageUrl> getListImageUrl(Long comicChapter) {
        Log.d(TAG, "[getListImageUrl] comicChapter: " + comicChapter);
        return mImageUrlDao.getBox()
                .query()
                .equal(ImageUrl_.comicChapter, comicChapter)
                .build()
                .find();
    }

    public ImageUrl load(long id) {
        Log.d(TAG, "[load] id: " + id);
        return mImageUrlDao.load(id);
    }

    public void updateOrInsert(List<ImageUrl> imageUrlList) {
        Log.d(TAG, "[updateOrInsert]");
        for (ImageUrl imageurl : imageUrlList) {
            if (imageurl.getId() == null) {
                insert(imageurl);
            } else {
                update(imageurl);
            }
        }
    }

    public void insertOrReplace(List<ImageUrl> imageUrlList) {
        Log.d(TAG, "[insertOrReplace]");
        for (ImageUrl imageurl : imageUrlList) {
            if (imageurl.getId() != null) {
                mImageUrlDao.insertOrReplace(imageurl);
            }
        }
    }

    public void update(ImageUrl imageurl) {
        Log.d(TAG, "[update] imageurl: " + imageurl);
        mImageUrlDao.update(imageurl);
    }

    public void deleteByKey(long key) {
        Log.d(TAG, "[deleteByKey] key: " + key);
        mImageUrlDao.deleteByKey(key);
    }

    public void insert(ImageUrl imageUrl) {
        Log.d(TAG, "[ImageUrl] imageUrl: " + imageUrl);
        long id = mImageUrlDao.insert(imageUrl);
        imageUrl.setId(id);
    }

}
