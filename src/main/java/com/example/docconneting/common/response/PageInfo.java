package com.example.docconneting.common.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PageInfo {
    private int pageNum;
    private int pageSize;
    private long totalElement;
    private int totalPage;

    @Builder
    public PageInfo(int pageNum, int pageSize, long totalElement, int totalPage) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalElement = totalElement;
        this.totalPage = totalPage;
    }
}
