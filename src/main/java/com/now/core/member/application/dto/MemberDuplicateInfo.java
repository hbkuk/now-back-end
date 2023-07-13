package com.now.core.member.application.dto;

import lombok.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 중복된 회원 정보를 나타내는 객체
 */
@Builder(toBuilder = true)
@ToString
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class MemberDuplicateInfo {

    private final boolean duplicateId;
    private final boolean duplicateNickname;

    /**
     * 중복된 필드에 대한 메시지 목록을 생성 후 반환
     *
     * @return 중복된 필드에 대한 메시지 목록
     */
    public List<String> generateDuplicateFieldMessages() {
        return Stream.of(
                        duplicateId ? "회원 아이디가 중복됩니다." : null,
                        duplicateNickname ? "회원 닉네임이 중복됩니다." : null
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 적어도 하나의 중복된 필드가 존재한다면 true 반환, 그렇지 않다면 false 반환
     *
     * @return 중복된 필드가 존재한다면 true 반환, 그렇지 않다면 false 반환
     */
    public boolean existsAtLeastOneDuplicate() {
         return duplicateId || duplicateNickname;
    }
 }
