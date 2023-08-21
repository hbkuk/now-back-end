package com.now.core.post.common.presentation.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("페이지 객체")
class PageTest {

    @ParameterizedTest
    @CsvSource(value = {"10:1", "10:3", "20:1", "20:3"}, delimiter = ':')
    @DisplayName("게시글의 총 개수가 0일 경우 maxPage, pageNo, startPage, endPage는 1로 설정된다")
    void calculatePageInfo_1(int recordsPerPage, int pageNo) {
        // given
        Page page = Page.of(recordsPerPage, pageNo);

        // when
        page.calculatePageInfo(0L);

        // then
        assertThat(page.getMaxPage()).isEqualTo(1);
        assertThat(page.getPageNo()).isEqualTo(1);
        assertThat(page.getStartPage()).isEqualTo(1);
        assertThat(page.getEndPage()).isEqualTo(1);
    }

    @ParameterizedTest
    @CsvSource(value = {"10:1", "10:3", "20:1", "20:3"}, delimiter = ':')
    @DisplayName("게시글의 총 개수가 0이 아닐경우 maxPage, pageNo, startPage, endPage는 계산된다")
    void calculatePageInfo_2(int recordsPerPage, int pageNo) {
        // given
        double totalPostCount = 100L;
        int blockPerPage = 5;
        Page page = Page.of(recordsPerPage, pageNo);

        // when
        page.calculatePageInfo(100L);

        // then
        assertThat(page.getMaxPage()).isEqualTo((int) Math.ceil(totalPostCount / recordsPerPage));
        assertThat(page.getPageNo()).isEqualTo(pageNo);
        assertThat(page.getStartPage()).isNotEqualTo(((int)(Math.ceil((double) pageNo / blockPerPage))- blockPerPage + 1));
        assertThat(page.getEndPage()).isNotEqualTo(1);
    }
}
