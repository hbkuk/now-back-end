package com.now.core.member.domain;

import com.now.core.member.domain.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 회원 정보를 관리하는 레포지토리
 */
@Repository
public class MemberRepository {

    public MemberMapper memberMapper;

    @Autowired
    public MemberRepository(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    /**
     * 전달받은 아이디에 해당하는 회원 정보를 조회 후 반환
     *
     * @param memberId 조회할 회원의 아이디
     * @return 조회된 회원 정보 (해당 아이디에 해당하는 회원가 없으면 null)
     */
    public Member findById(String memberId) {
        return memberMapper.findById(memberId);
    }

    /**
     * 회원 정보를 저장
     *
     * @param member 저장할 회원 정보
     */
    public void saveMember(Member member) {
        memberMapper.saveMember(member);
    }


    /**
     * 회원 정보를 수정
     *
     * @param member 수정할 회원 정보
     */
    public void updateMember(Member member) {
        memberMapper.updateMember(member);
    }

    /**
     * 전달받은 아이디가 데이터베이스에 존재한다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param memberId 확인할 회원 아이디
     * @return 회원 아이디의 존재 여부
     */
    public boolean existsById(String memberId) {
        return memberMapper.existsById(memberId);
    }

    /**
     * 전달받은 닉네임이 데이터베이스에 존재한다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param nickname 확인할 닉네임
     * @return 닉네임의 존재 여부
     */
    public boolean existsByNickname(String nickname) {
        return memberMapper.existsByNickname(nickname);
    }
}
