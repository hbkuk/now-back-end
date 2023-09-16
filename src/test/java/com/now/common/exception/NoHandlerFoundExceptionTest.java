package com.now.common.exception;

import com.now.config.document.utils.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@DisplayName("NoHandlerFoundException")
class NoHandlerFoundExceptionTest extends ControllerTest {

    @Test
    @DisplayName("존재하지 않는 url로 요청이 들어오면, 잘못된 경로(INVALID_PATH)를 의미하는 에러 코드를 응답한다")
    void noHandlerFoundException() throws Exception {
        String endpointPath = "/api/dkanshfoskxmfdj";

        mockMvc.perform(MockMvcRequestBuilders.get(endpointPath))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorType.INVALID_PATH.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ErrorType.INVALID_PATH.getMessage()));
    }

}
