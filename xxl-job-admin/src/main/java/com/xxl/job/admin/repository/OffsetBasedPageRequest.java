package com.xxl.job.admin.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.io.Serializable;

public class OffsetBasedPageRequest implements Pageable, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final int limit;
    private final long offset;
    private final Sort sort;

    public OffsetBasedPageRequest(long offset, int limit, Sort sort) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must not be less than zero");
        }
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must not be less than one");
        }
        this.limit = limit;
        this.offset = offset;
        this.sort = sort == null ? Sort.unsorted() : sort;
    }

    @Override
    public int getPageNumber() {
        return (int) (offset / limit);
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetBasedPageRequest(getOffset() + getPageSize(), getPageSize(), getSort());
    }

    @Override
    public Pageable previousOrFirst() {
        long newOffset = getOffset() - getPageSize();
        return newOffset < 0 ? first() : new OffsetBasedPageRequest(newOffset, getPageSize(), getSort());
    }

    @Override
    public Pageable first() {
        return new OffsetBasedPageRequest(0, getPageSize(), getSort());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetBasedPageRequest((long) pageNumber * getPageSize(), getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return offset > 0;
    }
}
