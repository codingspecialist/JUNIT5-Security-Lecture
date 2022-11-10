package site.metacoding.bank.config.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionEnum {
    WITHDRAW("출금"), DEPOSIT("입금"), TRANSFER("이체");

    private final String value;
}
