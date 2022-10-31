package site.metacoding.market.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleEnum {
    BUYER("구매자"), SELLER("판매자"), ADMIN("관리자");

    private final String value;
}
