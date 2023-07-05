package com.now.domain.comment;

import com.now.domain.permission.AccessPermission;
import com.now.domain.user.User;
import lombok.*;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 댓글 정보를 담고있는 도메인 객체
 *
 * @Builder(toBuilder = true)
 * : 빌더 패턴을 사용하여 객체를 생성합니다. toBuilder 옵션은 생성된 빌더 객체를 이용해 기존 객체를 복사하고 수정할 수 있도록 합니다.
 * @ToString : 객체의 문자열 표현을 자동으로 생성합니다. 주요 필드들의 값을 포함한 문자열을 반환합니다.
 * @Getter : 필드들에 대한 Getter 메서드를 자동으로 생성합니다.
 * @NoArgsConstructor(force = true)
 * : 매개변수가 없는 기본 생성자를 자동으로 생성합니다. MyBatis 또는 JPA 라이브러리에서는 기본 생성자를 필요로 합니다.
 * @AllArgsConstructor : 모든 필드를 매개변수로 받는 생성자를 자동으로 생성합니다.
 */
@Builder(toBuilder = true)
@ToString
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Comment {

    /**
     * 댓글의 고유 식별자
     */
    private final Long commentIdx;

    /**
     * 게시글을 작성자한 작성자의 고유 식별자
     */
    private final String authorId;

    /**
     * 댓글의 등록일자
     */
    private final LocalDateTime regDate;

    /**
     * 댓글의 내용
     */
    @Size(max = 2000, message = "댓글 내용은 최대 2000자까지 입력 가능합니다.")
    private String content;

    /**
     * 게시글의 고유 식별자
     */
    private Long postIdx;

    /**
     * 댓글을 삭제할 수 있다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param accessPermission 접근 권한을 확인하기 위한 AccessPermission 객체
     * @return 댓글을 삭제할 수 있다면 true 반환, 그렇지 않다면 false 반환
     */
    public boolean canDelete(AccessPermission accessPermission) {
        return accessPermission.hasAccess(this.authorId);
    }

    /**
     * 댓글을 수정할 수 있다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param user  유저 정보가 담긴 객체
     * @return      댓글을 수정할 수 있다면 true 반환, 그렇지 않다면 false 반환
     */
    public boolean canUpdate(User user) {
        return user.isSameUserId(this.authorId);
    }
}
