package com.now.core.post.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.Size;

/**
 * 질문 게시글의 답변 정보를 전달하는 데이터 전송 객체
 */
@Builder(toBuilder = true)
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Answer {

    private Long postIdx;

    @Size(min = 1, max = 2000, message = "{comment.content.size}")
    private final String answerContent;

    @JsonIgnore
    private String answerManagerId;

    @JsonIgnore
    private Integer answerManagerIdx;

    /**
     * 답변자 ID 업데이트
     *
     * @param answerManagerId 답변자 ID
     * @return 업데이트된 답변 객체
     */
    public Answer updateAnswerManagerId(String answerManagerId) {
        this.answerManagerId = answerManagerId;
        return this;
    }

    /**
     * 답변자 고유 식별자 업데이트
     *
     * @param answerManagerIdx 답변자 인덱스
     * @return 업데이트된 답변 객체
     */
    public Answer updateAnswerManagerIdx(Integer answerManagerIdx) {
        this.answerManagerIdx = answerManagerIdx;
        return this;
    }

    /**
     * 게시물 인덱스를 업데이트
     *
     * @param postIdx 게시물 인덱스
     * @return 업데이트된 답변 객체
     */
    public Answer updatePostIdx(Long postIdx) {
        this.postIdx = postIdx;
        return this;
    }
}
