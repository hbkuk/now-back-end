package com.now.config.fixtures.post;

import com.now.core.category.domain.constants.Category;
import com.now.core.post.domain.Notice;

import java.time.LocalDateTime;

public class NoticeFixture {

    public static final String SAMPLE_NICKNAME_1 = "YullManager";
    public static final String SAMPLE_TITLE_1 = "중요 공지사항";
    public static final String SAMPLE_CONTENT_1 = "안녕하세요. 중요한 공지사항입니다. 서비스 정책 변경으로 인해 모든 사용자는 반드시 비밀번호를 변경해야 합니다. 감사합니다.";

    public static final String SAMPLE_NICKNAME_2 = "HoonManager";
    public static final String SAMPLE_TITLE_2 = "서비스 이용 안내";
    public static final String SAMPLE_CONTENT_2 = "안녕하세요. 저희 서비스를 이용해 주셔서 감사합니다. 이용 중 궁금한 점이나 문제가 발생하면 언제든지 문의해주세요. 즐거운 하루 되세요!";

    public static Notice createNotice(Long postIdx, String nickName, String title, String content) {
        return Notice.builder()
                .postIdx(postIdx)
                .managerNickname(nickName)
                .category(Category.NEWS)
                .title(title)
                .regDate(LocalDateTime.now())
                .modDate(null)
                .content(content)
                .viewCount(1000)
                .likeCount(100)
                .dislikeCount(2)
                .pinned(true)
                .build();
    }

    public static Notice createNoticeForSave() {
        return Notice.builder()
                .category(Category.NEWS)
                .title(SAMPLE_TITLE_1)
                .content(SAMPLE_CONTENT_1)
                .pinned(true)
                .build();
    }

    public static String createNoticeJson(String category, String title, String content, boolean pinned) {
        return "{\"category\":\"" + category + "\",\"title\":\"" + title + "\",\"content\":\"" + content + "\",\"pinned\":" + pinned + "}";
    }
}
