package com.now.config.fixtures.post;

import com.now.core.attachment.presentation.dto.AttachmentResponse;
import com.now.core.category.domain.constants.Category;
import com.now.core.comment.domain.Comment;
import com.now.core.post.domain.Community;

import java.time.LocalDateTime;
import java.util.List;

public class CommunityFixture {

    public static final String SAMPLE_NICKNAME_1 = "Bob";
    public static final String SAMPLE_TITLE_1 = "비전공자로 살아남기 1";
    public static final String SAMPLE_CONTENT_1 = "비전공자로서 살아남는 것은 전공자와는 다소 다른 도전이 있을 수 있지만, 아래의 조언들을 따라가면 도움이 될 수 있습니다";

    public static final String SAMPLE_NICKNAME_2 = "Honi";
    public static final String SAMPLE_TITLE_2 = "개인 프로젝트 및 포트폴리오 구축";
    public static final String SAMPLE_CONTENT_2 = "자신이 배운 것들을 실제로 활용할 수 있는 개인 프로젝트를 진행해보세요. 개인 프로젝트를 통해 자신의 능력을 증명할 수 있고, 포트폴리오를 구축하는 데 도움이 됩니다.";

    public static Community createCommunity(Long postIdx, String nickName, String title, String content,
                                            List<AttachmentResponse> attachments, List<Comment> comments) {
        return Community.builder()
                .postIdx(postIdx)
                .memberNickname(nickName)
                .category(Category.COMMUNITY_STUDY)
                .title(title)
                .regDate(LocalDateTime.now())
                .modDate(null)
                .content(content)
                .viewCount(1000)
                .likeCount(100)
                .dislikeCount(2)
                .attachments(attachments)
                .comments(comments)
                .build();
    }

    public static Community createCommunity(String memberId) {
        return Community.builder()
                .postIdx(1L)
                .memberId(memberId)
                .memberNickname(SAMPLE_NICKNAME_1)
                .category(Category.COMMUNITY_STUDY)
                .title(SAMPLE_TITLE_1)
                .regDate(LocalDateTime.now())
                .modDate(null)
                .content(SAMPLE_CONTENT_1)
                .viewCount(1000)
                .likeCount(100)
                .dislikeCount(2)
                .build();
    }

    public static Community createCommunityForSave() {
        return Community.builder()
                .category(Category.COMMUNITY_STUDY)
                .title(SAMPLE_TITLE_1)
                .content(SAMPLE_CONTENT_1)
                .build();
    }

    public static Community createCommunity(String memberId, Category category) {
        return Community.builder()
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
}
