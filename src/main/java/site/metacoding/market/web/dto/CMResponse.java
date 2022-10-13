package site.metacoding.market.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.metacoding.market.enums.ResponseEnum;

@AllArgsConstructor
@Getter
public class CMResponse<T> {

    private final Integer code;
    private final String message;
    private final T content;

    public CMResponse(ResponseEnum respEnum, T content) {
        this.code = respEnum.getCode();
        this.message = respEnum.getMessage();
        this.content = content;
    }

    public CMResponse(ResponseEnum respEnum) {
        this.code = respEnum.getCode();
        this.message = respEnum.getMessage();
        this.content = null;
    }
}
