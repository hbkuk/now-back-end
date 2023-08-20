
package com.now.config.fixtures.post;

import com.now.core.attachment.presentation.dto.AttachmentResponse;
import com.now.core.category.domain.constants.Category;
import com.now.core.comment.domain.Comment;
import com.now.core.post.photo.domain.Photo;

import java.time.LocalDateTime;
import java.util.List;

public class PhotoFixture {

    public static final String SAMPLE_NICKNAME_1 = "Bob";
    public static final String SAMPLE_TITLE_1 = "순간을 담다: 사진으로 떠나는 여행";
    public static final String SAMPLE_CONTENT_1 = "여러분을 환영합니다! 이 사진 게시글에서는 우리의 시각적인 여정을 함께 합니다.";

    public static final String SAMPLE_NICKNAME_2 = "Honi";
    public static final String SAMPLE_TITLE_2 = "렌즈를 통해 본 세상: 아름다움의 발견";
    public static final String SAMPLE_CONTENT_2 = "이곳은 사진들의 환상적인 세계로 초대합니다.";

    public static Photo createPhoto(Long postIdx, String nickName, String title, String content,
                                            List<AttachmentResponse> attachments, List<Comment> comments) {
        return Photo.builder()
                .postIdx(postIdx)
                .memberNickname(nickName)
                .category(Category.DAILY_LIFE)
                .title(title)
                .regDate(LocalDateTime.now())
                .modDate(null)
                .content(content)
                .viewCount(1000)
                .likeCount(100)
                .dislikeCount(2)
                .attachments(attachments)
                .comments(comments)
                .thumbnailAttachmentIdx(1L)
                .build();
    }

    public static Photo createPhoto(String memberId) {
        return Photo.builder()
                .postIdx(1L)
                .memberId(memberId)
                .memberNickname(SAMPLE_NICKNAME_1)
                .category(Category.DAILY_LIFE)
                .title(SAMPLE_TITLE_1)
                .regDate(LocalDateTime.now())
                .modDate(null)
                .content(SAMPLE_CONTENT_1)
                .viewCount(1000)
                .likeCount(100)
                .dislikeCount(2)
                .build();
    }

    public static Photo createPhotoForSave() {
        return Photo.builder()
                .category(Category.DAILY_LIFE)
                .title(SAMPLE_TITLE_1)
                .content(SAMPLE_CONTENT_1)
                .thumbnailAttachmentIdx(1L)
                .build();
    }

    public static Photo createPhoto(String memberId, Category category) {
        return Photo.builder()
                .postIdx(1L)
                .memberId(memberId)
                .memberNickname(SAMPLE_NICKNAME_1)
                .category(category)
                .title(SAMPLE_TITLE_1)
                .regDate(LocalDateTime.now())
                .modDate(null)
                .content(SAMPLE_CONTENT_1)
                .viewCount(1000)
                .likeCount(100)
                .dislikeCount(2)
                .build();
    }

    public static Photo createPhotoForSave(Long memberIdx, String memberId, String memberNickname, Category category, String title, String content) {
        return Photo.builder()
                .category(category)
                .title(title)
                .content(content)
                .memberId(memberId)
                .memberIdx(memberIdx)
                .memberNickname(memberNickname)
                .build();
    }
}
