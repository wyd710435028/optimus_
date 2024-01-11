package com.unisound.optimus_visual.global.pagination;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PageInfo<T> implements Serializable {
    private static final long serialVersionUID = 8545991263226528798L;

    private List<T> records;

    private long total;

    private long size;

    private long current;

    private Map<String,Object> extra;

    public PageInfo() {
        this.records = Collections.emptyList();
        this.total = 0L;
        this.size = 10L;
        this.current = 1L;

    }

    public PageInfo(long current, long size) {
        this(current, size, 0L);
    }

    public PageInfo(long current, long size, long total) {
        this(current, size, total, true);
    }

    public PageInfo(long current, long size, boolean isSearchCount) {
        this(current, size, 0L, isSearchCount);
    }

    public PageInfo(long current, long size, long total, boolean isSearchCount) {
        this.records = Collections.emptyList();
        this.total = 0L;
        this.size = 10L;
        this.current = 1L;
        if (current > 1L) {
            this.current = current;
        }

        this.size = size;
        this.total = total;
    }


    public List<T> getRecords() {
        return this.records;
    }

    public PageInfo<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    public long getTotal() {
        return this.total;
    }

    public PageInfo<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public long getSize() {
        return this.size;
    }

    public PageInfo<T> setSize(long size) {
        this.size = size;
        return this;
    }

    public long getCurrent() {
        return this.current;
    }

    public PageInfo<T> setCurrent(long current) {
        this.current = current;
        return this;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}

