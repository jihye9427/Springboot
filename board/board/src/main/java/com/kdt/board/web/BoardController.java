package com.kdt.board.web;

import com.kdt.board.domain.board.svc.BoardSVC;
import com.kdt.board.domain.entity.Board;
import com.kdt.board.web.form.board.DetailForm;
import com.kdt.board.web.form.board.SaveForm;
import com.kdt.board.web.form.board.UpdateForm;
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
@RequestMapping("/boards")       // GET http://localhost:9080/products
@RequiredArgsConstructor
public class BoardController {
  final private BoardSVC boardSVC;

  //목록
  @GetMapping       // GET  http://localhost:9080/products
  public String findAll(Model model) {
    List<Board> list = boardSVC.findAll();
    model.addAttribute("list", list);
    return "board/all";   //view
  }

  //상품등록화면
  @GetMapping("/add")       // GET  http://localhost:9080/products/add
  public String addForm(Model model){
    model.addAttribute("saveForm",new SaveForm());
    return "board/add";  //view
  }


//  //상품등록처리
//  @PostMapping("/add")      // POST http://localhost:9080/products/add
//  public String add(
//      @Valid @ModelAttribute SaveForm saveForm,
//      BindingResult bindingResult,
//      RedirectAttributes redirectAttributes,
//      Model model
//  ){
//    log.info("pname={},price={},quantity={}",saveForm.getPname(),saveForm.getPrice(),saveForm.getQuantity());
//
//    //1)유효성 체크
//    //1-1) 어노테이션 기반의 필드 검증
//    if(bindingResult.hasErrors()){
//      log.info("bindingResult={}", bindingResult);
//      return "product/add";
//    }
//
//    //1-2-2) 글로벌오류 : 총액(상품수량 * 단가) 1000만원 초과 불과
//    if(saveForm.getPrice() * saveForm.getQuantity() > 10_000_000) {
//      bindingResult.reject("totalPrice","총액(상품수량 * 단가) 1000만원 초과 불가!");
//    }
//
//    if (bindingResult.hasErrors()) {
//      log.info("bindingResult={}", bindingResult);
//      return "product/add";
//    }

//    //2)정상로직
//    Board product = new Board();
//    board.setPname(saveForm.getPname());
//    board.setQuantity(saveForm.getQuantity());
//    board.setPrice(saveForm.getPrice());
//
//    Long pid = boardSVC.save(product);
//    redirectAttributes.addAttribute("id",pid);
//    return "redirect:/boards/{id}"; //http 응답메세지  상태라인 : 302
//    //               응답헤더 -> location : http://localhost:9080/products/2
//    //http 요청메세지  요청라인 : GET http://localhost:9080/products/2
//  }

  //상품조회(단건)
  @GetMapping("/{id}")      // GET http://localhost:9080/products/2?name=홍길동&age=20
  public String findById(
      @PathVariable("id") Long id,        // 경로변수 값을 읽어올때
      Model model
//      @RequestParam("name") String name,  // 쿼리파라미터 값을 읽어올때
//      @RequestParam("age") Long age
  ){

    log.info("id={}",id);
//    log.info("name={}",name);
//    log.info("age={}",age);

    Optional<Product> optionalProduct = productSVC.findById(id);
    Product findedProduct = optionalProduct.orElseThrow();

    DetailForm detailForm = new DetailForm();
    detailForm.setProductId(findedProduct.getProductId());
    detailForm.setPname(findedProduct.getPname());
    detailForm.setQuantity(findedProduct.getQuantity());
    detailForm.setPrice(findedProduct.getPrice());

    model.addAttribute("detailForm",detailForm);

    return "product/detailForm";   //상품상세화면
  }

  //상품삭제(단건)
  //  @GetMapping("/del?id=상품번호")   // GET http://localhost:9080/products/del?pid=상품번호
  @GetMapping("/{id}/del")   // GET http://localhost:9080/products/상품아이디/del
  public String deleteById(
      //@RequestParm("id") Long productId
      @PathVariable("id") Long productId) {

    int rows = productSVC.deleteById(productId);

    return "redirect:/products";      // 302 get redirectUrl: http://localhost:9080/products
  }

  //상품삭제(여러건)
  @PostMapping("/del")      // POST http://localhost:9080/products/del
  public String deleteByIds(@RequestParam("productIds") List<Long> productIds) {

    log.info("productIds={}", productIds);

    int rows = productSVC.deleteByIds(productIds);
    log.info("상품정보 {}-건 삭제됨!", rows);
    return "redirect:/products";
  }

  //상품수정화면
  @GetMapping("/{id}/edit")         // GET http://localhost:9080/2/edit
  public String updateForm(
      @PathVariable("id") Long productId,
      Model model
  ) {
    //1) 유효성체크
    //2) 상품조회
    Optional<Product> optionalProduct = productSVC.findById(productId);
    Product findedProduct = optionalProduct.orElseThrow();

    UpdateForm updateForm = new UpdateForm();
    updateForm.setProductId(findedProduct.getProductId());
    updateForm.setPname(findedProduct.getPname());
    updateForm.setQuantity(findedProduct.getQuantity());
    updateForm.setPrice(findedProduct.getPrice());

    model.addAttribute("updateForm",updateForm);
    return "product/updateForm";
  }

  //상품수정처리
  @PostMapping("/{id}/edit")         // POST http://localhost:9080/2/edit
  public String updateById(
      @PathVariable("id") Long productId,
      UpdateForm updateForm,
      RedirectAttributes redirectAttributes
  ){
    log.info("id={}", productId);
    log.info("updateForm={}",updateForm);

    Product product = new Product();
    product.setProductId(updateForm.getProductId());
    product.setPname(updateForm.getPname());
    product.setQuantity(updateForm.getQuantity());
    product.setPrice(updateForm.getPrice());

    int rows = productSVC.updateById(productId, product);

    redirectAttributes.addAttribute("id",productId);
    return "redirect:/products/{id}";  // 302 get redirectUrl-> http://localhost/products/id
  }

  //
  @ResponseBody
  @GetMapping("/test1")   // GET http://localhost:9080/products/test1
  public String test1() {
    return "test1";
  }
}
