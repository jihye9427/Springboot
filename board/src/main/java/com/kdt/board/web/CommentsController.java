package com.kdt.comments.web;

import com.kdt.comments.domain.comments.svc.CommentsSVC;
import com.kdt.comments.domain.entity.comments;
import com.kdt.comments.web.form.comments.DetailForm;
import com.kdt.comments.web.form.comments.SaveForm;
import com.kdt.comments.web.form.comments.UpdateForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/comments")       // GET http://localhost:9090/comments
public class CommentsController {
  final private CommentsSVC commentsSVC;

  //댓글 등록화면
  @GetMapping("/add")       // GET  http://localhost:9090/comments/add
  public String addForm(Model model){
    model.addAttribute("saveForm",new SaveForm());
    return "comments/addForm";  //view
  }

  //게시글 등록처리
  @PostMapping("/add")      // POST http://localhost:9090/comments/add
  public String add(
      @Valid @ModelAttribute SaveForm saveForm,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      Model model
  ){
    log.info("title={},content={},writer={}",saveForm.getTitle(),saveForm.getContent(),saveForm.getWriter());

    //1. 유효성 체크
    //2. 게시글 저장
    if(bindingResult.hasErrors()){
      log.info("bindingResult={}", bindingResult);
      return "comments/addForm";
    }

    Comments comments = new Comments();
    comments.setWriter(saveForm.getWriter());
    comments.setContent(saveForm.getContent());

    Long pid = commentsSVC.save(comments);
    log.info("게시글 저장 후 id={}", pid);
    redirectAttributes.addAttribute("id",pid);
    return "redirect:/commentss/{id}"; //http 응답메세지  상태라인 : 302
    //               응답헤더 -> location : http://localhost:9090/commentss/2
    //http 요청메세지  요청라인 : GET http://localhost:9090/commentss/2
  }

  //댓글 상세조회
  @GetMapping("/{id}")      // GET http://localhost:9090/commentss/2?name=홍길동&age=20
  public String findById(
      @PathVariable("id") Long id,        // 경로변수 값을 읽어올때
      Model model
  ){

    log.info("id={}",id);

    Optional<comments> optionalcomments = commentsSVC.findById(id);
    comments findedcomments = optionalcomments.orElseThrow();

    DetailForm detailForm = new DetailForm();
    detailForm.setCommentsId(findedcomments.getCommentsId());
    detailForm.setTitle(findedcomments.getTitle());
    detailForm.setContent(findedcomments.getContent());
    detailForm.setWriter(findedcomments.getWriter());
    detailForm.setCreated_date(findedcomments.getCreated_date());


    model.addAttribute("detailForm",detailForm);

    return "comments/detailForm";   //상품상세화면
  }


  //게시글 삭제(단건)
  @GetMapping("/{id}/del")   // GET http://localhost:9090/commentss/상품아이디/del
  public String deleteById(
      @PathVariable("id") Long commentsId) {

    int rows = commentsSVC.deleteById(commentsId);

    return "redirect:/commentss";      // 302 get redirectUrl: http://localhost:9080/commentss
  }

  //댓글 수정화면
  @GetMapping("/{id}/edit")         // GET http://localhost:9090/2/edit
  public String updateForm(
      @Valid @PathVariable("id") Long commentsId,
      Model model
  ) {
    //1. 유효성체크
    //2. 상품조회
    Optional<comments> optionalcomments = commentsSVC.findById(commentsId);
    comments findedcomments = optionalcomments.orElseThrow();

    UpdateForm updateForm = new UpdateForm();
    updateForm.setcommentsId(findedcomments.getcommentsId());
    updateForm.setTitle(findedcomments.getTitle());
    updateForm.setContent(findedcomments.getContent());
    updateForm.setWriter(findedcomments.getWriter());

    model.addAttribute("updateForm",updateForm);
    return "comments/updateForm";
  }

  //댓글 수정처리
  @PostMapping("/{id}/edit")         // POST http://localhost:9090/2/edit
  public String updateById(
      @PathVariable("id") Long commentsId,
      @Valid @ModelAttribute UpdateForm updateForm,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes
  ){
    log.info("id={}", commentsId);
    log.info("updateForm={}",updateForm);

    //1. 유효성 체크
    if(bindingResult.hasErrors()){
      log.info("bindingResult={}", bindingResult);

      return "comments/updateForm";
    }

    if (bindingResult.hasErrors()) {
      log.info("bindingResult={}", bindingResult);
      return "comments/updateForm";
    }

    comments comments = new comments();
    comments.setcommentsId(updateForm.getcommentsId());
    comments.setTitle(updateForm.getTitle());
    comments.setContent(updateForm.getContent());
    comments.setWriter(updateForm.getWriter());

    int rows = commentsSVC.updateById(commentsId, comments);

    // 상세 페이지로 바로이동
    redirectAttributes.addAttribute("id",commentsId);
    return "redirect:/commentss/{id}";  // 302 get redirectUrl-> http://localhost/commentss/id
  }

  //
  @ResponseBody
  @GetMapping("/test1")   // GET http://localhost:9090/commentss/test1
  public String test1() {
    return "test1";
  }
}
