package com.kdt.board.web.api;

import lombok.Data;

import java.util.Map;

@Data
public class ApiResponse <T> {
  private final Header header;
  private final T body;

  private ApiResponse(Header header, T body) {
    this.header = header;
    this.body = body;
  }

  @Data
  private static class Header{
    private final String rtcd;
    private final String rtmsg;
    private final Map<String,String> details;

    public Header(String rtcd, String rtmsg, Map<String,String> details) {
      this.rtcd = rtcd;
      this.rtmsg = rtmsg;
      this.details = details;
    }
  }

  public static <T> ApiResponse<T> of(String rtcd, String rtmsg, Map details, T body){
    return new ApiResponse<>( new Header(rtcd, rtmsg, details ), body);
  }
}
