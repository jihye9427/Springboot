package com.kdt.board.domain.member.svc;

import com.kdt.board.domain.entity.Member;

import java.util.Optional;

public interface MemberSVC {
  
  // 가입
  Member join(Member member);
  
  // 회원 존재 유무
  boolean isMember(String email);
  
  // 회원 조회
  Optional<Member> findByMemberId(Long memberId);
  Optional<Member> findByEmail(String email);


}
