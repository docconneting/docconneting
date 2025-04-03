package com.example.docconneting.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class Response<T> {
    private T data;
    private PageInfo page;

    private Response(T data) {
        this.data = data;
    }

    private Response(T data, PageInfo page) {
        this.data = data;
        this.page = page;
    }

    // Pagination이 적용안 된 response 형식
    public static <T> Response<T> of(T data) {
        return new Response<>(data);
    }

    // Pagination이 적용된 response 형식
    public static <T> Response<T> of(T data, PageInfo page) {
        return new Response<>(data, page);
    }
}
