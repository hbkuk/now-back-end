package com.now.core.post.application;

import com.now.NowApplication;
import com.now.common.exception.ErrorType;
import com.now.config.fixtures.comment.CommentFixture;
import com.now.core.category.domain.constants.Category;
import com.now.core.category.exception.InvalidCategoryException;
import com.now.core.comment.domain.Comment;
import com.now.core.comment.domain.CommentRepository;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.member.exception.InvalidMemberException;
import com.now.core.post.domain.Community;
import com.now.core.post.domain.PostRepository;
import com.now.core.post.exception.CannotDeletePostException;
import com.now.core.post.exception.CannotUpdatePostException;
import com.now.core.post.exception.InvalidPostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static com.now.config.fixtures.member.MemberFixture.createMember;
import static com.now.config.fixtures.post.CommunityFixture.createCommunity;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = NowApplication.class)
@DisplayName("공지 서비스 객체는")
// TODO: 추후 매니저별 권한 부여 후 테스트 코드 작성
class NoticeServiceTest {

}
