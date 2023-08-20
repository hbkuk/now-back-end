package com.now.config.fixtures.post.dto;

import com.now.core.post.inquiry.presentation.dto.Answer;

public class AnswerFixture {

    public static String SAMPLE_ANSWER_CONTENT_1 = "안녕하세요. 답변드리도록 하겠습니다.....";
    public static String SAMPLE_ANSWER_CONTENT_2 = "안녕하세요. 수정된 답변드리도록 하겠습니다.....";

    public static Answer createAnswer(Long postIdx, Integer answerManagerIdx, String answerManagerId, String answerContent) {
        return Answer.builder()
                .postIdx(postIdx)
                .answerManagerIdx(answerManagerIdx)
                .answerManagerId(answerManagerId)
                .answerContent(answerContent)
                .build();
    }
}
