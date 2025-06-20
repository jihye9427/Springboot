package com.kdt.board.web;

import com.kdt.board.domain.entity.Member;
import com.kdt.board.domain.member.dao.MemberDAO;
import com.kdt.board.web.api.ApiResponse;
import com.kdt.board.web.api.ApiResponseCode;
import com.kdt.board.web.form.login.LoginForm;
import com.kdt.board.web.form.login.LoginMember;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

  private final MemberDAO memberDAO;

  //로그인 화면
  @GetMapping("/login")       // GET http://localhost:9090/login
  public String loginForm(Model model){
    model.addAttribute("loginForm", new LoginForm());
    return "login/loginForm";
  }

  //로그인 처리
  @PostMapping("/login")     // POST http://localhost:9090/login
  public String login(
      @Valid @ModelAttribute LoginForm loginForm,
      BindingResult bindingResult,
      HttpServletRequest request
  ) {
    log.info("loginForm={}", loginForm);

    //1) LoginForm객체 바인딩 오류
    if(bindingResult.hasErrors()) {
      return "login/loginForm";
    }

    //2) 회원존재유무 체크
    if(!memberDAO.isExist(loginForm.getEmail())){
      bindingResult.rejectValue("email", null, "회원정보가 존재하지 않습니다.");
      return "login/loginForm";
    }

    //3) 비밀번호 일치여부 체크
    //3-1) 회원 찾기
    Optional<Member> optionalMember = memberDAO.findByEmail(loginForm.getEmail());
    Member member = optionalMember.get();
    log.info("member={}", member);
    //3-2) 비밀번호 확인
    if(!loginForm.getPasswd().equals(member.getPasswd())){
      bindingResult.rejectValue("passwd", null,"비밀번호가 다릅니다.");
      return "login/loginForm";
    }

    //4) 세션 생성
    // 세션이 존재하면 해당 세션을 가져오고, 없으면 신규로 생성
    HttpSession session = request.getSession(true);

    //5) 세션에 회원번호 저장
    LoginMember loginMember = new LoginMember(
        member.getMemberId(),
        member.getEmail(),
        member.getNickname(),
        member.getGubun()
    );
    session.setAttribute("loginMember", loginMember);


    return "redirect:/";    // 초기화면
  }

  //로그아웃 처리
  @DeleteMapping("logout")   // DELETE http://localhost:9090/logout
  @ResponseBody
  public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request){

    ResponseEntity<ApiResponse<String>> res = null;

    //1) 세션정보 가져오기
    HttpSession session = request.getSession(false);

    //2) 세션제거
    if (session != null) {
      session.invalidate();
      ApiResponse<String> stringApiResponse = ApiResponse.of(ApiResponseCode.SUCCESS, "세션 제거됨!");
      res = ResponseEntity.ok(stringApiResponse);
    }
    return res;
  }








}
