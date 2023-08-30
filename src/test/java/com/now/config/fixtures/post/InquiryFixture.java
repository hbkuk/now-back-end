package com.now.config.fixtures.post;

import com.now.core.category.domain.constants.Category;
import com.now.core.post.inquiry.domain.Inquiry;
import com.now.core.post.inquiry.domain.constants.InquiryStatus;
import com.now.core.post.photo.domain.Photo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class InquiryFixture {

    public static final String SAMPLE_NICKNAME_1 = "Yull";
    public static final String SAMPLE_TITLE_1 = "문의 사항이 있어서 연락드립니다.";
    public static final String SAMPLE_CONTENT_1 = "해당 게시글에 대해 몇 가지 궁금한 점이 있어서 문의드립니다.";

    public static final String SAMPLE_NICKNAME_2 = "Hoon";
    public static final String SAMPLE_TITLE_2 = "서비스 이용에 문의드립니다.";
    public static final String SAMPLE_CONTENT_2 = "게시글에 몇 가지 궁금한 점이 있어서 문의드립니다.";

    public static final String SAMPLE_MANAGER_NICKNAME_1 = "GangManager";

    public static Inquiry createSecretInquiry(Long postIdx, String nickName, String title, String content) {
        return Inquiry.builder()
                .postIdx(postIdx)
                .memberNickname(nickName)
                .category(Category.SERVICE)
                .title(title)
                .regDate(LocalDateTime.now())
                .modDate(null)
                .content(content)
                .viewCount(1000)
                .likeCount(100)
                .dislikeCount(2)
                .secret(true)
                .answerManagerNickname(SAMPLE_MANAGER_NICKNAME_1)
                .answerContent("안녕하세요. 답변드리도록 하겠습니다.....")
                .answerRegDate(String.valueOf(LocalDateTime.now().plus(2, ChronoUnit.DAYS)))
                .inquiryStatus(InquiryStatus.COMPLETE)
                .build();
    }

    public static Inquiry createNonSecretInquiry(Long postIdx, String nickName, String title, String content) {
        return Inquiry.builder()
                .postIdx(postIdx)
                .memberNickname(nickName)
                .category(Category.SERVICE)
                .title(title)
                .regDate(LocalDateTime.now())
                .modDate(null)
                .content(content)
                .viewCount(1000)
                .likeCount(100)
                .dislikeCount(2)
                .secret(false)
                .inquiryStatus(InquiryStatus.INCOMPLETE)
                .build();
    }

    public static Inquiry createInquiryForSave() {
        return Inquiry.builder()
                .category(Category.SERVICE)
                .title(SAMPLE_TITLE_1)
                .content(SAMPLE_CONTENT_1)
                .secret(true)
                .password("1234")
                .inquiryStatus(InquiryStatus.INCOMPLETE)
                .build();
    }

    public static Inquiry createInquiryForSave(String memberId) {
        return Inquiry.builder()
                .category(Category.SERVICE)
                .title(SAMPLE_TITLE_1)
                .content(SAMPLE_CONTENT_1)
                .secret(true)
                .password("1234")
                .inquiryStatus(InquiryStatus.INCOMPLETE)
                .build();
    }

    public static Inquiry createInquiryForSave(String memberId, Category category) {
        return Inquiry.builder()
                .category(category)
                .title(SAMPLE_TITLE_1)
                .content(SAMPLE_CONTENT_1)
                .secret(true)
                .password("1234")
                .inquiryStatus(InquiryStatus.INCOMPLETE)
                .build();
    }

    public static Inquiry createSecretInquiry(String memberId) {
        return Inquiry.builder()
                .postIdx(1L)
                .title("제목")
                .memberId(memberId)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .content("내용")
                .viewCount(0)
                .likeCount(0)
                .dislikeCount(0)
                .secret(true)
                .password("0736")
                .category(Category.SERVICE)
                .inquiryStatus(InquiryStatus.INCOMPLETE)
                .build();
    }

    public static Inquiry createSecretInquiry(String memberId, Category category) {
        return Inquiry.builder()
                .postIdx(1L)
                .title("제목")
                .memberId(memberId)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .content("내용")
                .viewCount(0)
                .likeCount(0)
                .dislikeCount(0)
                .secret(true)
                .category(category)
                .password("0736")
                .inquiryStatus(InquiryStatus.INCOMPLETE)
                .build();
    }

    public static Inquiry createSecretInquiryWithAnswer(String memberId, Category category) {
        return Inquiry.builder()
                .postIdx(1L)
                .title("제목")
                .memberId(memberId)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .content("내용")
                .viewCount(0)
                .likeCount(0)
                .dislikeCount(0)
                .secret(true)
                .category(category)
                .password("0736")
                .answerManagerNickname("manager1")
                .inquiryStatus(InquiryStatus.COMPLETE)
                .build();
    }

    public static Inquiry createNonSecretInquiry(String memberId) {
        return Inquiry.builder()
                .postIdx(1L)
                .title("제목")
                .answerManagerIdx(memberId)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .content("내용")
                .viewCount(0)
                .likeCount(0)
                .dislikeCount(0)
                .secret(false)
                .category(Category.SERVICE)
                .inquiryStatus(InquiryStatus.INCOMPLETE)
                .build();
    }

    public static Inquiry createNonSecretInquiry(String memberId, Category category) {
        return Inquiry.builder()
                .postIdx(1L)
                .title("제목")
                .answerManagerIdx(memberId)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .content("내용")
                .viewCount(0)
                .likeCount(0)
                .dislikeCount(0)
                .secret(false)
                .category(category)
                .inquiryStatus(InquiryStatus.INCOMPLETE)
                .build();
    }

    public static Inquiry createInquiryForSave(Long memberIdx, String memberId, String memberNickname, Category category, String title, String content, Boolean serect) {
        return Inquiry.builder()
                .category(category)
                .title(title)
                .content(content)
                .memberId(memberId)
                .memberIdx(memberIdx)
                .memberNickname(memberNickname)
                .secret(serect)
                .inquiryStatus(InquiryStatus.INCOMPLETE)
                .build();
    }

    public static Inquiry createInquiryForSave(Long memberIdx, String memberId, String memberNickname, Category category, String title, String content, Boolean serect, String password) {
        return Inquiry.builder()
                .category(category)
                .title(title)
                .content(content)
                .memberId(memberId)
                .memberIdx(memberIdx)
                .memberNickname(memberNickname)
                .secret(serect)
                .password(password)
                .inquiryStatus(InquiryStatus.INCOMPLETE)
                .build();
    }

    public static Inquiry createInquiry(Long postIdx, Long memberIdx, String memberId, Category category, String title, String content, Boolean serect, String password) {
        return Inquiry.builder()
                .postIdx(postIdx)
                .category(category)
                .title(title)
                .content(content)
                .memberIdx(memberIdx)
                .memberId(memberId)
                .secret(serect)
                .password(password)
                .build();
    }
}
