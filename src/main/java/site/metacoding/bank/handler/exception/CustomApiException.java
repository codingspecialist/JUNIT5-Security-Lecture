package site.metacoding.bank.handler.exception;

import site.metacoding.bank.enums.ResponseEnum;

public class CustomApiException extends RuntimeException {

    private final ResponseEnum responseEnum;

    public CustomApiException(ResponseEnum responseEnum) {
        super(responseEnum.getMessage());
        this.responseEnum = responseEnum;
    }

    public ResponseEnum getResponseEnum() {
        return responseEnum;
    }
}