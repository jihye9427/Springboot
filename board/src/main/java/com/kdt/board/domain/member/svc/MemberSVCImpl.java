package com.kdt.board.domain.member.svc;

import com.kdt.board.domain.entity.Member;
import com.kdt.board.domain.member.dao.MemberDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberSVCImpl implements MemberSVC{
  private final MemberDAO memberDAO;

  @Override
  public Member join(Member member) {
    return memberDAO.insertMember(member);
  }

  @Override
  public boolean isMember(String email) {
    return memberDAO.isExist(email);
  }

  @Override
  public Optional<Member> findByMemberId(Long memberId) {
    return memberDAO.findByMemberId(memberId);
  }

  @Override
  public Optional<Member> findByEmail(String email) {
    return memberDAO.findByEmail(email);
  }
}
