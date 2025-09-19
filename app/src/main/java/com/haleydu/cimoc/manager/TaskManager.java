package com.haleydu.cimoc.manager;

import com.haleydu.cimoc.component.AppGetter;
import com.haleydu.cimoc.model.Task;
import com.haleydu.cimoc.model.TaskDao;
import com.haleydu.cimoc.model.TaskDao.Properties;

import org.greenrobot.daocompat.query.QueryBuilder;

import java.util.Collection;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Hiroshi on 2016/9/4.
 */
public class TaskManager {

    private static TaskManager mInstance;

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
        return mTaskDao.queryBuilder().list();
    }

    public List<Task> listValid() {
        return mTaskDao.queryBuilder()
                .where(Properties.Max.notEqual(0))
                .list();
    }

    public List<Task> list(long key) {
        return mTaskDao.queryBuilder()
                .where(Properties.Key.equal(key))
                .list();
    }

    public Observable<List<Task>> listInRx(long key) {
        /*
        return mTaskDao.queryBuilder()
                .where(Properties.Key.equal(key))
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mTaskDao.queryBuilder()
                        .where(Properties.Key.equal(key))
                        .list()
        );
    }

    public Observable<List<Task>> listInRx() {
        /*
        return mTaskDao.queryBuilder()
                .rx()
                .list();
         */
        return Observable.fromCallable(() ->
                mTaskDao.queryBuilder()
                        .list()
        );
    }

    public void insert(Task task) {
        long id = mTaskDao.insert(task);
        task.setId(id);
    }

    public void insertInTx(Iterable<Task> entities) {
        mTaskDao.insertInTx((Collection<Task>) entities);
    }

    public void update(Task task) {
        mTaskDao.update(task);
    }

    public void delete(Task task) {
        mTaskDao.delete(task);
    }

    public void delete(long id) {
        mTaskDao.deleteByKey(id);
    }

    public void deleteInTx(Iterable<Task> entities) {
        mTaskDao.deleteInTx((Collection<Task>) entities);
    }

    public void deleteByComicId(long id) {
        /*
        mTaskDao.queryBuilder()
                .where(Properties.Key.equal(id))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
         */
        mTaskDao.queryBuilder()
                .where(Properties.Key.equal(id))
                .build()
                .remove();
    }

    public void insertIfNotExist(final Iterable<Task> entities) {
        mTaskDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (Task task : entities) {
                    QueryBuilder<Task> builder = mTaskDao.queryBuilder()
                            .where(Properties.Key.equal(task.getKey()), Properties.Path.equal(task.getPath()));
                    if (builder.unique() == null) {
                        mTaskDao.insert(task);
                    }
                }
            }
        });
    }

}
