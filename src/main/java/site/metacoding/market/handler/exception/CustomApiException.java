package site.metacoding.market.handler.exception;

import site.metacoding.market.enums.ResponseEnum;

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