package site.metacoding.market.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PurchaseEnum {
    PURCHASE("구매"), CANCEL("취소");

    private final String state;
}
