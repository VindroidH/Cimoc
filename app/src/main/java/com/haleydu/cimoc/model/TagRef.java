package com.haleydu.cimoc.model;

import androidx.annotation.NonNull;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by Hiroshi on 2016/10/10.
 */
@Entity
public class TagRef {

    @Id(assignable = true)
    private Long id;
    /**
     * Tag.id
     */
    private long tid = 0L;
    /**
     * Comic.id
     */
    private long cid = 0L;

    public TagRef(Long id, long tid, long cid) {
        this.id = id;
        this.tid = tid;
        this.cid = cid;
    }

    public TagRef() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTid() {
        return this.tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public long getCid() {
        return this.cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    @NonNull
    @Override
    public String toString() {
        return "TagRef[id: " + id + ", tid: " + tid + ", cid: " + cid + "]";
    }
}
