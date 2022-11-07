package site.metacoding.bank.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import site.metacoding.bank.config.exceptions.CustomApiException;
import site.metacoding.bank.config.exceptions.CustomValidationApiException;
import site.metacoding.bank.dto.ResponseDto;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(CustomValidationApiException.class)
    public ResponseEntity<?> validationApiException(CustomValidationApiException e) {
        return new ResponseEntity<>(new ResponseDto<>(400, e.getMessage(), e.getErrorMap()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<?> apiException(CustomApiException e) {
        return new ResponseEntity<>(new ResponseDto<>(e.getResponseEnum(), null), HttpStatus.BAD_REQUEST);
    }

}