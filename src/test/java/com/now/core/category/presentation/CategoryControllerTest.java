package com.now.core.category.presentation;

import com.now.common.mapper.EnumMapperValue;
import com.now.config.document.utils.RestDocsTestSupport;
import com.now.core.category.domain.constants.PostGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("모든 카테고리 게시글 조회")
    void getAllCategories() throws Exception {
        given(enumMapperFactory.get("PostGroup"))
                .willReturn(Arrays.stream(PostGroup.values())
                        .map(EnumMapperValue::new)
                        .collect(Collectors.toList()));

        // when, then
        ResultActions resultActions =
                mockMvc.perform(RestDocumentationRequestBuilders.get("/api/categories"))
                        .andExpect(status().isOk());

        // then
        resultActions
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("[].").type(ARRAY).description("게시글 그룹 목록"),
                                fieldWithPath("[].code").type(STRING).description("게시글 그룹"),
                                fieldWithPath("[].title").type(STRING).description("카테고리")
                        )));
    }
}