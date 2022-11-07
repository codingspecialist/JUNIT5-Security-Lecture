package site.metacoding.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.metacoding.bank.config.enums.ResponseEnum;

@Getter
public class ResponseDto<T> {

    private Integer code;
    private String message;
    private T data;

    public ResponseDto(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseDto(ResponseEnum respEnum, T data) {
        this.code = respEnum.getCode();
        this.message = respEnum.getMessage();
        this.data = data;
    }
}
