package com.now.domain.file;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

/**
 * 파일을 나타내는 도메인 객체
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
public class File {

    /**
     * 파일의 고유 식별자
     */
    private final Long fileIdx;

    /**
     * 서버 디렉토리에 저장된 파일 이름(JSON 직렬화 시 숨김 처리)
     */
    @JsonIgnore
    private final String savedFileName;

    /**
     * 사용자가 알고 있는 실제 파일 이름
     */
    private final OriginalFileName originalFileName;

    /**
     * 파일의 확장자명
     */
    private final FileExtension fileExtension;

    /**
     * 파일의 크기
     */
    private final FileSize fileSize;

    /**
     *  게시글의 고유 식별자
     */
    private Long postIdx;

    /**
     * 게시글 번호가 업데이트 된 File 객체를 리턴합니다.
     *
     * @param postIdx 게시글 번호
     */
    public File updatePostIdx(Long postIdx) {
        this.postIdx = postIdx;
        return this;
    }
}
