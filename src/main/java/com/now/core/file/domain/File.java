package com.now.core.file.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.now.core.file.domain.wrapped.FileExtension;
import com.now.core.file.domain.wrapped.FileSize;
import com.now.core.file.domain.wrapped.OriginalFileName;
import lombok.*;

/**
 * 파일을 나타내는 도메인 객체
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
    private Long memberPostIdx;

    /**
     * 게시글 번호가 업데이트 된 File 객체를 리턴
     *
     * @param memberPostIdx 게시글 번호
     */
    public File updateMemberPostIdx(Long memberPostIdx) {
        this.memberPostIdx = memberPostIdx;
        return this;
    }
}
