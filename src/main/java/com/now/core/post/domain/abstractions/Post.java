package com.now.core.post.domain.abstractions;

import com.now.core.comment.domain.Comment;
import com.now.core.category.domain.constants.PostGroup;
import com.now.core.file.domain.File;
import com.now.core.manager.domain.Manager;
import com.now.core.category.domain.constants.Category;
import com.now.core.member.domain.Member;
import com.now.core.post.domain.PostValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 추상 클래스로서 {@link Manager} 혹은 {@link Member}가 작성한 게시글을 의미하는 도메인 객체
 */
@SuperBuilder(toBuilder = true)
@ToString
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public abstract class Post {

    /**
     * 게시글의 고유 식별자
     */
    private Long postIdx;

    /**
     * 카테고리
     */
    private final Category category;

    /**
     * 게시글의 제목
     */
    @Size(groups = {PostValidationGroup.register.class}, min = 1, max = 100, message = "게시글의 제목은 1글자 이상, 100글자 이하")
    private final String title;

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
    @Size(groups = {PostValidationGroup.register.class}, min = 1, max = 100, message = "공지사항의 내용은 1글자 이상, 4000글자 이하")
    private final String content;

    /**
     * 게시글의 조회수
     */
    private final Integer viewCount;

    /**
     * 게시글의 좋아요 수
     */
    private final Integer likeCount;

    /**
     * 게시글 싫어요 수
     */
    private final Integer dislikeCount;

    /**
     * 파일 (file 테이블에서 가져옴)
     */
    private final List<File> files;

    /**
     * 댓글 (comment 테이블에서 가져옴)
     */
    private final List<Comment> comments;

    /**
     * 게시글 그룹을 반환
     *
     * @return 게시글 그룹
     */
    public abstract PostGroup getPostGroup();
}
