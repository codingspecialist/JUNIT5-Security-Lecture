package site.metacoding.market.handler;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import site.metacoding.market.handler.exception.CustomApiException;
import site.metacoding.market.handler.exception.CustomValidationApiException;
import site.metacoding.market.init.MyLog;
import site.metacoding.market.web.dto.ResponseDto;

@RestControllerAdvice
public class ResponseHandler {

    @ExceptionHandler(CustomValidationApiException.class)
    public HttpEntity<?> validationApiException(CustomValidationApiException e) {
        MyLog.info("validationApiException");
        return new ResponseEntity<>(new ResponseDto<>(1, e.getMessage(), e.getErrorMap()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomApiException.class)
    public HttpEntity<?> apiException(CustomApiException e) {
        MyLog.info("apiException");
        return new ResponseEntity<>(new ResponseDto<>(e.getResponseEnum()), HttpStatus.BAD_REQUEST);
    }

}