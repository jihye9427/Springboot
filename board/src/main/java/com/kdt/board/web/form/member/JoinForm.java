package com.kdt.board.web.form.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class JoinForm {
  @NotBlank(message = "이메일 입력은 필수 입니다!")
  @Email(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$",message = "이메일 형식에 맞지 않습니다.")
  @Size(min=10,max=30,message = "이메일 크기는 10자~30자 사이여야 합니다.")
  private String email;           //  EMAIL	VARCHAR2(50 BYTE)
  @NotBlank(message = "비밀번호 입력은 필수 입니다!")
  private String passwd;          //  PASSWD	VARCHAR2(12 BYTE)
  @NotBlank(message = "비밀번호확인 입력은 필수 입니다!")
  private String passwdChk;          //  PASSWD	VARCHAR2(12 BYTE)
  private String tel;             //  TEL	VARCHAR2(13 BYTE)
  @NotBlank(message = "별칭 입력은 필수 입니다!")
  private String nickname;        //  NICKNAME	VARCHAR2(30 BYTE)
  private String gender;          //  GENDER	VARCHAR2(6 BYTE)
  private List<String> hobby;     //  HOBBY	VARCHAR2(300 BYTE)
  private String region;          //  REGION	VARCHAR2(11 BYTE)
}
