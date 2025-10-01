package com.sparta.forusmarket.common.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
public class ApiPageResponse<T> implements Serializable {

    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private List<T> data;

    private ApiPageResponse(int page, int size, int totalPages, long totalElements, List<T> data) {
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.data = data;
    }

    /**
     * 성공적인 요청에 대한 페이징 응답을 반환하는 메서드 주어진 데이터를 포함하여 HTTP 200 OK 상태 코드와 함께 응답을 반환
     *
     * @param pagedData 요청 성공 시 반환할 페이징 데이터
     * @return HTTP 200 OK 응답과 함께 성공 데이터가 포함된 ApiPageResponse
     */
    public static <T> ResponseEntity<ApiPageResponse<T>> success(RestPage<T> pagedData) {
        return ResponseEntity.ok(fromPage(pagedData));
    }

    public static <T> ResponseEntity<ApiPageResponse<T>> success(Page<T> pagedData) {
        return ResponseEntity.ok(fromPage(pagedData));
    }

    private static <T> ApiPageResponse<T> fromPage(RestPage<T> pagedData) {
        return new ApiPageResponse<>(
                pagedData.getPageable().getPageNumber(),
                pagedData.getPageable().getPageSize(),
                pagedData.getTotalPages(),
                pagedData.getTotalElements(),
                pagedData.getContent()
        );
    }

    private static <T> ApiPageResponse<T> fromPage(Page<T> pagedData) {
        return new ApiPageResponse<>(
                pagedData.getPageable().getPageNumber(),
                pagedData.getPageable().getPageSize(),
                pagedData.getTotalPages(),
                pagedData.getTotalElements(),
                pagedData.getContent()
        );
    }

    @JsonIgnoreProperties(ignoreUnknown = true, value = {"pageable"})
    public static class RestPage<T> extends PageImpl<T> {
        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public RestPage(@JsonProperty("content") List<T> content,
                        @JsonProperty("number") int page,
                        @JsonProperty("size") int size,
                        @JsonProperty("totalElements") long total) {
            super(content, PageRequest.of(page, size), total);
        }

        public RestPage(Page<T> page) {
            super(page.getContent(), page.getPageable(), page.getTotalElements());
        }
    }
}
