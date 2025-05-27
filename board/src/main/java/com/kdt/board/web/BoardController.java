package com.kdt.board.web;

import com.kdt.board.domain.board.svc.BoardSVC;
import com.kdt.board.domain.entity.Board;
import com.kdt.board.web.form.board.DetailForm;
import com.kdt.board.web.form.board.SaveForm;
import com.kdt.board.web.form.board.UpdateForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/boards")       // GET http://localhost:9090/boards
@RequiredArgsConstructor
public class BoardController {
  final private BoardSVC boardSVC;

  //게시글 전체목록 조회
  @GetMapping       // GET  http://localhost:9090/boards
  public String findAll(Model model) {
    List<Board> list = boardSVC.findAll();
    model.addAttribute("list", list);

    return "board/all";
  }

  //게시글 작성화면
  @GetMapping("/add")       // GET  http://localhost:9090/boards/add
  public String addForm(Model model){
    model.addAttribute("saveForm",new SaveForm());
    return "board/addForm";  //view
  }

  //게시글 작성처리
  @PostMapping("/add")      // POST http://localhost:9090/boards/add
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
      return "board/addForm";
    }

    Board board = new Board();
    board.setTitle(saveForm.getTitle());
    board.setContent(saveForm.getContent());
    board.setWriter(saveForm.getWriter());

    Long pid = boardSVC.save(board);
    log.info("게시글 저장 후 id={}", pid);
    redirectAttributes.addAttribute("id",pid);
    return "redirect:/board/{id}"; //http 응답메세지  상태라인 : 302
    //               응답헤더 -> location : http://localhost:9090/boards/2
    //http 요청메세지  요청라인 : GET http://localhost:9090/boards/2
  }


  //게시글 상세조회
  @GetMapping("/{id}")      // GET http://localhost:9090/boards/2?name=홍길동&age=20
  public String findById(
      @PathVariable("id") Long id,        // 경로변수 값을 읽어올때
      Model model
  ){

    log.info("id={}",id);

    Optional<Board> optionalBoard = boardSVC.findById(id);
    Board board = optionalBoard.orElseThrow();

    DetailForm detailForm = new DetailForm();
    BeanUtils.copyProperties(board, detailForm);

    log.info("detailForm={}", detailForm);
    model.addAttribute("detailForm", detailForm);

    return "board/detailForm";
  }


  //게시글 삭제(단건)
  @GetMapping("/{id}/del")   // GET http://localhost:9090/boards/상품아이디/del
  public String deleteById(
      @PathVariable("id") Long boardId) {

    int rows = boardSVC.deleteById(boardId);

    return "redirect:/board";      // 302 get redirectUrl: http://localhost:9080/boards
  }

  //게시글 삭제(여러건)
  @PostMapping("/del")      // POST http://localhost:9090/boards/del
  public String deleteByIds(@RequestParam("boardIds") List<Long> boardIds) {

    log.info("boardIds={}", boardIds);

    int rows = boardSVC.deleteByIds(boardIds);
    log.info("상품정보 {}-건 삭제됨!", rows);
    return "redirect:/board";
  }

  //게시글 수정화면
  @GetMapping("/{id}/edit")         // GET http://localhost:9090/2/edit
  public String updateForm(
      @Valid @PathVariable("id") Long boardId,
      Model model
  ) {
    //1. 유효성체크
    //2. 상품조회
    Optional<Board> optionalBoard = boardSVC.findById(boardId);
    Board findedBoard = optionalBoard.orElseThrow();

    UpdateForm updateForm = new UpdateForm();
    updateForm.setBoardId(findedBoard.getBoardId());
    updateForm.setTitle(findedBoard.getTitle());
    updateForm.setContent(findedBoard.getContent());
    updateForm.setWriter(findedBoard.getWriter());

    model.addAttribute("updateForm",updateForm);
    return "board/updateForm";
  }

  //게시글 수정처리
  @PostMapping("/{id}/edit")         // POST http://localhost:9090/2/edit
  public String updateById(
      @PathVariable("id") Long boardId,
      @Valid @ModelAttribute UpdateForm updateForm,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes
  ){
    log.info("id={}", boardId);
    log.info("updateForm={}",updateForm);

    //1. 유효성 체크
    if(bindingResult.hasErrors()){
      log.info("bindingResult={}", bindingResult);

      return "board/updateForm";
    }

    if (bindingResult.hasErrors()) {
      log.info("bindingResult={}", bindingResult);
      return "board/updateForm";
    }

    Board board = new Board();
    board.setBoardId(updateForm.getBoardId());
    board.setTitle(updateForm.getTitle());
    board.setContent(updateForm.getContent());
    board.setWriter(updateForm.getWriter());

    int rows = boardSVC.updateById(boardId, board);

    // 상세 페이지로 바로이동
    redirectAttributes.addAttribute("id",boardId);
    return "redirect:/board/{id}";  // 302 get redirectUrl-> http://localhost/boards/id
  }

  //
  @ResponseBody
  @GetMapping("/test1")   // GET http://localhost:9090/boards/test1
  public String test1() {
    return "test1";
  }
}
