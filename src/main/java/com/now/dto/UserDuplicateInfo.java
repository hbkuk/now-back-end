package com.now.dto;

import lombok.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 중복된 유저 정보를 나타내는 객체
 *
 * @Builder(toBuilder = true)
 *     : 빌더 패턴을 사용하여 객체를 생성합니다. toBuilder 옵션은 생성된 빌더 객체를 이용해 기존 객체를 복사하고 수정할 수 있도록 합니다.
 * @ToString
 *     : 객체의 문자열 표현을 자동으로 생성합니다. 주요 필드들의 값을 포함한 문자열을 반환합니다.
 * @Getter
 *     : 필드들에 대한 Getter 메서드를 자동으로 생성합니다.
 * @NoArgsConstructor(force = true)
 *     : 매개변수가 없는 기본 생성자를 자동으로 생성합니다. MyBatis 또는 JPA 라이브러리에서는 기본 생성자를 필요로 합니다.
 * @AllArgsConstructor
 *     : 모든 필드를 매개변수로 받는 생성자를 자동으로 생성합니다.
 */
@Builder(toBuilder = true)
@ToString
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class UserDuplicateInfo {

    private final boolean duplicateId;
    private final boolean duplicateNickname;

    /**
     * 중복된 필드에 대한 메시지 목록을 생성 후 반환
     *
     * @return 중복된 필드에 대한 메시지 목록
     */
    public List<String> generateDuplicateFieldMessages() {
        return Stream.of(
                        duplicateId ? "유저 아이디가 중복됩니다." : null,
                        duplicateNickname ? "유저 닉네임이 중복됩니다." : null
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
