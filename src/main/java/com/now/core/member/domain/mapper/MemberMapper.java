package com.now.core.member.domain.mapper;

import com.now.core.member.domain.Member;
import org.apache.ibatis.annotations.Mapper;

/**
 * 회원 정보에 접근하는 매퍼 인터페이스
 */
@Mapper
public interface MemberMapper {

    /**
     * 회원 정보를 저장
     *
     * @param member 저장할 회원 정보
     */
    void saveMember(Member member);

    /**
     * 회원 정보 수정
     * 
     * @param member 수정할 회원 정보 
     */
    void updateMember(Member member);

    /**
     * 전달받은 아이디가 데이터베이스에 존재한다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param memberId 확인할 아이디
     * @return 아이디의 존재 여부
     */
    boolean existsById(String memberId);

    /**
     * 전달받은 닉네임이 데이터베이스에 존재한다면 true 반환, 그렇지 않다면 false 반환
     *
     * @param nickname 확인할 닉네임
     * @return 닉네임의 존재 여부
     */
    boolean existsByNickname(String nickname);

    /**
     * 전달받은 아이디에 해당하는 회원 정보를 조회 후 반환
     *
     * @param memberId 조회할 회원의 아이디
     * @return 조회된 회원 정보 (해당 아이디에 해당하는 회원이 없으면 null)
     */
    Member findById(String memberId);
}
