package com.haleydu.cimoc.model;

import androidx.annotation.NonNull;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by Hiroshi on 2016/10/10.
 */
@Entity
public class Tag {

    @Id(assignable = true)
    private Long id;
    private String title = "";

    public Tag(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Tag() {
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Tag && ((Tag) o).id.equals(id);
    }

    @Override
    public int hashCode() {
        return id == null ? super.hashCode() : id.hashCode();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NonNull
    @Override
    public String toString() {
        return "Tag[id: " + id + ", title: " + title + "]";
    }
}
