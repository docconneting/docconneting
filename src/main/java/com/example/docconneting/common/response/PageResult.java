package com.example.docconneting.common.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResult<T> {
    private final List<T> content;
    private final PageInfo pageInfo;

    public PageResult(List<T> content, PageInfo pageInfo) {
        this.content = content;
        this.pageInfo = pageInfo;
    }

}

