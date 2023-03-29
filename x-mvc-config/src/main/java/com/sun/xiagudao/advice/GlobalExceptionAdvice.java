package com.sun.xiagudao.advice;

import com.sun.xiagudao.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = Exception.class)
    public CommonResponse<Object> handlerCommerceException(HttpServletRequest request, Exception exception) {
        CommonResponse<Object> commonResponse = new CommonResponse<>(-1, "ERROR");
        commonResponse.setData(exception.getMessage());
        return commonResponse;
    }
}
