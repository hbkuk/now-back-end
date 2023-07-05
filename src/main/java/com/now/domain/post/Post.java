package com.now.domain.post;

import com.now.domain.comment.Comment;
import com.now.domain.file.File;
import com.now.domain.manager.Manager;
import com.now.domain.user.User;
import com.now.exception.CannotDeletePostException;
import com.now.exception.CannotUpdatePostException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 정보를 담고있는 도메인 객체
 *
 * @SuperBuilder(toBuilder = true) : 상속 구조에서 부모 클래스의 빌더 패턴을 자동으로 생성해주는 기능.
 * toBuilder 옵션은 생성된 빌더 객체를 이용해 기존 객체를 복사하고 수정할 수 있도록 합니다.
 * @ToString : 객체의 문자열 표현을 자동으로 생성합니다. 주요 필드들의 값을 포함한 문자열을 반환합니다.
 * @Getter : 필드들에 대한 Getter 메서드를 자동으로 생성합니다.
 * @NoArgsConstructor(force = true)
 * : 매개변수가 없는 기본 생성자를 자동으로 생성합니다. MyBatis 또는 JPA 라이브러리에서는 기본 생성자를 필요로 합니다.
 * @AllArgsConstructor : 모든 필드를 매개변수로 받는 생성자를 자동으로 생성합니다.
 */
@SuperBuilder(toBuilder = true)
@ToString
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Post {

    /**
     * 게시글의 고유 식별자
     */
    private final Long postIdx;

    /**
     * 하위 코드 번호
     */
    @NotBlank(message = "카테고리 선택 필수")
    private final int subCodeIdx;

    /**
     * 하위 코드명 (FROM 하위 코드 테이블)
     */
    private final int subCodeName;

    /**
     * 게시글의 제목
     */
    @Size(min = 1, max = 100, message = "게시글의 제목은 1글자 이상, 100글자 이하")
    private final String title;

    /**
     * 게시글을 작성한 작성자의 아이디 혹은 관리자의 아이디(FROM 유저 테이블, 관리자 테이블)
     */
    private final String authorId;

    /**
     * 게시글 등록일자
     */
    private final LocalDateTime regDate;

    /**
     * 게시글 수정일자
     */
    private final LocalDateTime modDate;

    /**
     * 게시글의 내용
     */
    @Size(min = 1, max = 100, message = "공지사항의 내용은 1글자 이상, 4000글자 이하")
    private final String content;

    /**
     * 게시글의 조회수
     */
    private final int viewCount;

    /**
     * 게시글의 좋아요 수
     */
    private final int likeCount;

    /**
     * 게시글 싫어요 수
     */
    private final int dislikeCount;

    /**
     * 현재 사용자가 작성한 글인지 여부 (true: 현재 사용자의 글)
     */
    private final boolean isCurrentUserPost;

    /**
     * 파일 (file 테이블에서 가져옴)
     */
    private final List<File> files;

    /**
     * 댓글 (comment 테이블에서 가져옴)
     */
    private final List<Comment> comments;

    /**
     * 게시글을 삭제할 수 있다면 true 반환, 그렇지 않다면 예외를 던짐
     *
     * @param object    객체
     * @param comments 댓글 정보가 담긴 객체
     * @return 게시글을 삭제할 수 있다면 true 반환, 그렇지 않다면 false 반환
     */
    public boolean canDelete(Object object, List<Comment> comments) {
        if (object instanceof Manager) {
            return true;
        }

        User user = (User) object;
        if(user != null) {

            if (!user.isSameUserId(this.authorId)) {
                throw new CannotDeletePostException("다른 사용자가 작성한 게시글을 삭제할 수 없습니다.");
            }

            for (Comment comment : comments) {
                if (!comment.canDelete(user)) {
                    throw new CannotDeletePostException("다른 사용자가 작성한 댓글이 있으므로 해당 게시글을 삭제할 수 없습니다.");
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 게시글을 수정할 수 있다면 true를 반환, 그렇지 않다면 예외를 던짐
     *
     * @param object   객체
     * @return 게시글을 수정할 수 있다면 true를 반환, 그렇지 않다면 예외를 던짐
     */
    public boolean canUpdate(Object object) {
        if (object instanceof Manager) {
            return true;
        }

        User user = (User) object;
        if(user != null) {
            if (!user.isSameUserId(this.authorId)) {
                throw new CannotUpdatePostException("다른 사용자가 작성한 게시글을 수정할 수 없습니다.");
            }
            return true;
        }
        return false;
    }

    /**
     * 전달된 유저 객체의 메서드를 호출해 현재 객체 필드의 userId와 동일하다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param user 유저 정보가 담긴 객체
     * @return 현재 객체 필드의 userId와 동일하다면 true 반환, 그렇지 않다면 false 반환
     */
    public boolean isSameUserId(User user) {
        return user.isSameUserId(this.authorId);
    }
}
