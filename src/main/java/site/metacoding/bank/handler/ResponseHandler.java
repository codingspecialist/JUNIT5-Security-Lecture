package site.metacoding.bank.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import site.metacoding.bank.dto.ResponseDto;
import site.metacoding.bank.handler.exception.CustomApiException;
import site.metacoding.bank.handler.exception.CustomValidationApiException;

@RestControllerAdvice
public class ResponseHandler {

    @ExceptionHandler(CustomValidationApiException.class)
    public ResponseEntity<?> validationApiException(CustomValidationApiException e) {
        return new ResponseEntity<>(new ResponseDto<>(1, e.getMessage(), e.getErrorMap()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<?> apiException(CustomApiException e) {
        return new ResponseEntity<>(new ResponseDto<>(e.getResponseEnum()), HttpStatus.BAD_REQUEST);
    }

}