package com.now.core.member.domain.repository;

import com.now.config.annotations.RepositoryTest;
import com.now.core.member.domain.Member;
import com.now.core.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.now.config.fixtures.member.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@DisplayName("회원 레포지토리")
class MemberRepositoryTest {

    @Autowired
    protected MemberRepository memberRepository;

    @Test
    @DisplayName("회원 정보를 수정할때 이름(name)과 닉네임(nickname)이 수정된다")
    void update_only_name_and_nickname() {
        // given
        String memberId = "member1";
        Member member = createMember(
                memberId,
                "홍당무",
                "Javaman");
        Member updatedMember = createMember(
                memberId,
                "홍당무",
                "Javagirl");

        memberRepository.saveMember(member);
        memberRepository.updateMember(updatedMember);

        // when
        Member actualMember = memberRepository.findById(memberId);

        // then
        assertThat(actualMember.getId()).isEqualTo(memberId);
        assertThat(actualMember.getNickname()).isEqualTo(updatedMember.getNickname());
        assertThat(actualMember.getName()).isEqualTo(updatedMember.getName());
    }
}
