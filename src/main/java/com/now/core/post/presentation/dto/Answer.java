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

    @Size(min = 1, max = 2000, message = "답변의 내용은 1글자 이상, 2000글자 이하")
    private final String answerContent;

    @JsonIgnore
    private String answerManagerId;

    @JsonIgnore
    private Long answerManagerIdx;


    public Answer updateaAswerManagerId(String answerManagerId) {
        this.answerManagerId = answerManagerId;
        return this;
    }

    public Answer updateaAswerManagerIdx(Long answerManagerIdx) {
        this.answerManagerIdx = answerManagerIdx;
        return this;
    }
}
