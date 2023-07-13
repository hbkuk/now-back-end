package com.now.core.post.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Min;

/**
 * 페이지 정보를 담는 객체
 */
@Data
public class PageCondition {

    /**
     * 페이지당 레코드 수
     */
    @JsonProperty
    private final Integer recordsPerPage = 5;

    /**
     * 블록당 페이지 수
     */
    @JsonProperty
    private final Integer blockPerPage = 5;

    /**
     * 페이지 번호
     */
    @Nullable
    @Min(value = 1, message = "페이지 번호는 1보다 큰 숫자여야 합니다")
    private Integer pageNo;

    /**
     * 레코드 시작 인덱스
     */
    @JsonProperty
    private Integer recordStartIndex;

    /**
     * 최대 페이지 수
     */
    @JsonProperty
    private Integer maxPage;

    /**
     * 시작 페이지
     */
    @JsonProperty
    private Integer startPage;

    /**
     * 종료 페이지
     */
    @JsonProperty
    private Integer endPage;

    /**
     * 페이지 번호를 기반으로 Page 객체 생성
     *
     * @param pageNo 페이지 번호
     */
    public PageCondition(Integer pageNo) {
        if( pageNo != null ) {
            this.pageNo = pageNo;
            this.recordStartIndex = (pageNo - 1) * this.recordsPerPage;
        }
        if( pageNo == null ) {
            this.pageNo = 1;
            this.recordStartIndex = 0;
        }
    }

    /**
     * 전체 게시글 수를 기반으로 페이지 정보를 계산
     *
     * @param totalBoardCount 전체 게시글 수
     * @return 계산된 페이지 정보를 포함한 현재 객체
     */
    public PageCondition calculatePaginationInfo(int totalBoardCount) {
        this.maxPage = updateMaxPage(totalBoardCount);
        this.startPage = updateStartPage();
        this.endPage = updateEndPage();
        return this;
    }

    /**
     * 종료 페이지를 업데이트
     *
     * @return 업데이트된 종료 페이지
     */
    private int updateEndPage() {
        int endPage = startPage + this.blockPerPage - 1;
        if (endPage > this.maxPage) {
            endPage = maxPage;
        }
        return endPage;
    }

    /**
     * 시작 페이지를 업데이트
     *
     * @return 업데이트된 시작 페이지
     */
    private int updateStartPage() {
        return (((int) (Math.ceil((double) pageNo / this.blockPerPage))) - 1) * this.blockPerPage + 1;
    }

    /**
     * 최대 페이지 수를 업데이트
     *
     * @param totalBoardCount 전체 게시글 수
     * @return 업데이트된 최대 페이지 수
     */
    private int updateMaxPage(double totalBoardCount) {
        return (int) (Math.ceil(totalBoardCount / this.recordsPerPage));
    }
}
