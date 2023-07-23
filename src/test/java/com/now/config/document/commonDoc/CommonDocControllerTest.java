package com.now.config.document.commonDoc;

import com.now.config.document.utils.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommonDocControllerTest extends RestDocsTestSupport {

    @Test
    public void errorSample() throws Exception {
        String requestBody = "{\"name\": \"honi132\", \"email\": \"testPassword1!\"}";
        mockMvc.perform(
                        post("/test/error")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andDo(
                        restDocs.document(
                                responseFields(
                                        fieldWithPath("message").description("에러 메시지"),
                                        fieldWithPath("code").description("Error Code"),
                                        fieldWithPath("errors").description("Error 값 배열 값"),
                                        fieldWithPath("errors[0].field").description("문제 있는 필드"),
                                        fieldWithPath("errors[0].value").description("문제가 있는 값"),
                                        fieldWithPath("errors[0].reason").description("문재가 있는 이유")
                                )
                        )
                )
        ;
    }
}
