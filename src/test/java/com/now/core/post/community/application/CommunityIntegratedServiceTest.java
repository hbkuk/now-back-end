package com.now.core.post.community.application;

import com.now.config.annotations.RepositoryTest;
import com.now.core.attachment.domain.AttachmentRepository;
import com.now.core.comment.domain.Comment;
import com.now.core.comment.domain.CommentRepository;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import com.now.core.post.common.application.dto.AddNewAttachments;
import com.now.core.post.common.application.dto.UpdateExistingAttachments;
import com.now.core.post.community.domain.Community;
import com.now.core.post.community.domain.repository.CommunityRepository;
import com.now.core.post.common.presentation.dto.Condition;
import com.now.core.post.common.presentation.dto.constants.Sort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static com.now.config.fixtures.attachment.AttachmentFixture.createMockMultipartFile;
import static com.now.config.fixtures.comment.CommentFixture.createCommentByMemberId;
import static com.now.config.fixtures.member.MemberFixture.createMember;
import static com.now.config.fixtures.post.CommunityFixture.createCommunityForSave;
import static com.now.config.fixtures.post.CommunityFixture.createCommunityForUpdate;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@DisplayName("커뮤니티 통합 서비스 객체")
class CommunityIntegratedServiceTest {

    @Autowired
    private CommunityIntegratedService communityIntegratedService;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Test
    @DisplayName("커뮤니티 게시글이 정상적으로 등록된다")
    void registerCommunity() {
        // given
        String memberId = "tester1";
        Member member = createMember(memberId);

        Community community = createCommunityForSave(memberId);

        MultipartFile[] multipartFiles = new MultipartFile[]
                {createMockMultipartFile("attachment2.jpg"), createMockMultipartFile("attachment2.jpg")};

        Condition condition = Condition.builder()
                .sort(Sort.LATEST)
                .build();

        // when
        memberRepository.saveMember(member);
        communityIntegratedService.registerCommunity(community, multipartFiles);

        List<Community> communities = communityRepository.findAllCommunity(condition.updatePage());

        // then
        assertThat(communities).hasSize(1);
    }

    @Test
    @DisplayName("커뮤니티 게시글이 정상적으로 수정된다")
    void updateCommunity_exist_notDeletedIndexes() {
        // given
        Long postIdx = 1L;
        String memberId = "tester1";
        Member member = createMember(memberId);

        Community community = createCommunityForSave(memberId);
        MultipartFile[] multipartFiles = new MultipartFile[]
                {createMockMultipartFile("attachment2.jpg"), createMockMultipartFile("attachment2.jpg")};

        Community updatedCommunity = createCommunityForUpdate(postIdx, memberId);
        List<Long> notDeletedIndexes = Arrays.asList(1L, 2L);
        MultipartFile[] newMultipartFiles = new MultipartFile[]
                {createMockMultipartFile("attachment2.jpg"), createMockMultipartFile("attachment2.jpg")};

        Condition condition = Condition.builder()
                .sort(Sort.LATEST)
                .build();

        // when
        memberRepository.saveMember(member);
        communityIntegratedService.registerCommunity(community, multipartFiles);

        Community c = communityRepository.findCommunity(1L);

        communityIntegratedService.updateCommunity(updatedCommunity,
                AddNewAttachments.of(null, newMultipartFiles),
                UpdateExistingAttachments.of(null, notDeletedIndexes));

        List<Community> communities = communityRepository.findAllCommunity(condition.updatePage());
        List<Long> attachmentResponses = attachmentRepository.findAllIndexesByPostIdx(postIdx);

        // then
        assertThat(communities).hasSize(1);
        assertThat(attachmentResponses).hasSize(4);
    }

    @Test
    @DisplayName("커뮤니티 게시글이 정상적으로 수정된다")
    void updateCommunity_notExist_notDeletedIndexes() {
        // given
        Long postIdx = 1L;
        String memberId = "tester1";
        Member member = createMember(memberId);

        Community community = createCommunityForSave(memberId);

        Community updatedCommunity = createCommunityForUpdate(postIdx, memberId);
        MultipartFile[] newMultipartFiles = new MultipartFile[]
                {createMockMultipartFile("attachment2.jpg"), createMockMultipartFile("attachment2.jpg")};

        Condition condition = Condition.builder()
                .sort(Sort.LATEST)
                .build();

        // when
        memberRepository.saveMember(member);
        communityIntegratedService.registerCommunity(community, null);

        communityIntegratedService.updateCommunity(updatedCommunity,
                AddNewAttachments.of(null, newMultipartFiles),
                UpdateExistingAttachments.of(null, null));

        List<Community> communities = communityRepository.findAllCommunity(condition.updatePage());
        List<Long> attachmentResponses = attachmentRepository.findAllIndexesByPostIdx(postIdx);

        // then
        assertThat(communities).hasSize(1);
        assertThat(attachmentResponses).hasSize(2);
    }

    @Test
    @DisplayName("커뮤니티 게시글이 정상적으로 삭제된다")
    void deleteCommunity() {
        // given
        Long postIdx = 1L;
        String memberId = "tester1";
        Member member = createMember(memberId);

        Community community = createCommunityForSave(memberId);
        MultipartFile[] multipartFiles = new MultipartFile[]
                {createMockMultipartFile("attachment2.jpg"), createMockMultipartFile("attachment2.jpg")};
        List<Comment> comments = List.of(createCommentByMemberId(postIdx, 1L, memberId), createCommentByMemberId(postIdx, 1L, memberId));

        Condition condition = Condition.builder()
                .sort(Sort.LATEST)
                .build();

        // when
        memberRepository.saveMember(member);
        communityIntegratedService.registerCommunity(community, multipartFiles);
        comments.forEach(comment -> commentRepository.saveCommentByMember(comment));

        List<Community> communities = communityRepository.findAllCommunity(condition.updatePage());
        List<Long> attachmentResponses = attachmentRepository.findAllIndexesByPostIdx(postIdx);

        // then
        assertThat(communities).hasSize(1);
        assertThat(attachmentResponses).hasSize(2);

        // when
        communityIntegratedService.deleteCommunity(postIdx, memberId);
        attachmentRepository.findAllIndexesByPostIdx(postIdx);

        // then
        assertThat(communityRepository.findAllCommunity(condition.updatePage())).hasSize(0);
        assertThat(attachmentRepository.findAllIndexesByPostIdx(postIdx)).hasSize(0);
        assertThat(commentRepository.findAllByPostIdx(postIdx)).hasSize(0);

    }
}

