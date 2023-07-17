package com.now.core.post.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.Size;

@Builder(toBuilder = true)
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Answer {

    private Long postIdx;

    @Size(min = 1, max = 2000)
    private final String answerContent;

    @JsonIgnore
    private String answerManagerId;

    @JsonIgnore
    private Long answerManagerIdx;


    public Answer updateAnswerManagerId(String answerManagerId) {
        this.answerManagerId = answerManagerId;
        return this;
    }

    public Answer updateAnswerManaegrIdx(Long answerManagerIdx) {
        this.answerManagerIdx = answerManagerIdx;
        return this;
    }
}
