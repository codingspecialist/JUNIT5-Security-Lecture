package site.metacoding.bank.config.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountEnum {
    DISABLED(false), ACTIVE(true);

    private Boolean value;
}
