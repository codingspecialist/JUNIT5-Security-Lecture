package site.metacoding.market.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionEnum {
    TRANSFER("이체"), ATM("자동화기기");

    private final String gubun;
}
