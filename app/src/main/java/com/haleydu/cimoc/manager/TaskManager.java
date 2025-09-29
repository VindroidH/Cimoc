package com.haleydu.cimoc.manager;

import android.util.Log;

import com.haleydu.cimoc.component.AppGetter;
import com.haleydu.cimoc.model.Task;
import com.haleydu.cimoc.model.TaskDao;
import com.haleydu.cimoc.model.Task_;

import java.util.Collection;
import java.util.List;

import io.objectbox.query.QueryBuilder;
import rx.Observable;

/**
 * Created by Hiroshi on 2016/9/4.
 */
public class TaskManager {
    private final static String TAG = "Cimoc-TaskManager";
    private static volatile TaskManager mInstance;

    private final TaskDao mTaskDao;

    private TaskManager(AppGetter getter) {
        mTaskDao = getter.getAppInstance().getDaoSession().getTaskDao();
    }

    public static TaskManager getInstance(AppGetter getter) {
        if (mInstance == null) {
            synchronized (TaskManager.class) {
                if (mInstance == null) {
                    mInstance = new TaskManager(getter);
                }
            }
        }
        return mInstance;
    }

    public List<Task> list() {
        Log.d(TAG, "[list]");
        return mTaskDao.getBox().query().build().find();
    }

    public List<Task> listValid() {
        Log.d(TAG, "[listValid]");
        return mTaskDao.getBox()
                .query()
                .notEqual(Task_.max, 0)
                .build()
                .find();
    }

    public List<Task> list(long key) {
        Log.d(TAG, "[list] key: " + key);
        return mTaskDao.getBox()
                .query()
                .equal(Task_.key, key)
                .build()
                .find();
    }

    public Observable<List<Task>> listInRx(long key) {
        Log.d(TAG, "[listInRx] key: " + key);
        /*
        return mTaskDao.queryBuilder()
                .where(Properties.Key.equal(key))
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mTaskDao.getBox()
                        .query()
                        .equal(Task_.key, key)
                        .build()
                        .find()
        );
    }

    public Observable<List<Task>> listInRx() {
        Log.d(TAG, "[listInRx]");
        /*
        return mTaskDao.queryBuilder()
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mTaskDao.getBox().query().build().find()
        );
    }

    public void insert(Task task) {
        Log.d(TAG, "[insert] task: " + task);
        long id = mTaskDao.insert(task);
        task.setId(id);
    }

    public void insertInTx(Iterable<Task> entities) {
        Log.d(TAG, "[insertInTx] entities");
        mTaskDao.insertInTx((Collection<Task>) entities);
    }

    public void update(Task task) {
        Log.d(TAG, "[update] task: " + task);
        mTaskDao.update(task);
    }

    public void delete(Task task) {
        Log.d(TAG, "[delete] task: " + task);
        mTaskDao.delete(task);
    }

    public void delete(long id) {
        Log.d(TAG, "[delete] id: " + id);
        mTaskDao.deleteByKey(id);
    }

    public void deleteInTx(Iterable<Task> entities) {
        Log.d(TAG, "[deleteInTx]");
        mTaskDao.deleteInTx((Collection<Task>) entities);
    }

    public void deleteByComicId(long id) {
        Log.d(TAG, "[deleteByComicId] id: " + id);
        /*
        mTaskDao.queryBuilder()
                .where(Properties.Key.equal(id))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
         */
        mTaskDao.getBox()
                .query()
                .equal(Task_.key, id)
                .build()
                .remove();
    }

    public void insertIfNotExist(final Iterable<Task> entities) {
        Log.d(TAG, "[insertIfNotExist]");
        mTaskDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (Task task : entities) {
                    QueryBuilder<Task> builder = mTaskDao.getBox()
                            .query()
                            .equal(Task_.key, task.getKey())
                            .equal(Task_.path, task.getPath(), QueryBuilder.StringOrder.CASE_SENSITIVE);
                    if (builder.build().count() == 0) {
                        mTaskDao.insert(task);
                    }
                }
            }
        });
    }

}
