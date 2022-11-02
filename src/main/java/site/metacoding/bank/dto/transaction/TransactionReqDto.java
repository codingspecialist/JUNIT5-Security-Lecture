package site.metacoding.bank.dto.transaction;

import lombok.Getter;
import lombok.Setter;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.enums.TransactionEnum;

public class TransactionReqDto {
    @Getter
    @Setter
    public static class WithdrawReqDto {
        private Long withdrawAccountId; // 출금 계좌
        private Long amount; // 금액
        private String gubun; // 고정값 (계좌에서 ATM 출금)

        public Transaction toEntity(Account withdrawAccount) {
            return Transaction.builder()
                    .withdrawAccount(withdrawAccount)
                    .depositAccount(null)
                    .amount(amount)
                    .gubun(TransactionEnum.valueOf(gubun))
                    .build();
        }
    }

    @Getter
    @Setter
    public static class DepositReqDto {
        private Long depositAccountId;
        private Long amount;
        private String gubun; // 고정값 (ATM에서 계좌로 입금)

        public Transaction toEntity(Account depositAccount) {
            return Transaction.builder()
                    .withdrawAccount(null)
                    .depositAccount(depositAccount)
                    .amount(amount)
                    .gubun(TransactionEnum.valueOf(gubun))
                    .build();
        }
    }

    @Getter
    @Setter
    public static class TransperReqDto {
        private Long withdrawAccountId; // 출금 계좌
        private Long depositAccountId; // 입금 계좌
        private Long amount; // 금액
        private String gubun; // 고정값 (계좌에서 계좌로 이체)

        public Transaction toEntity(Account withdrawAccount, Account depositAccount) {
            return Transaction.builder()
                    .withdrawAccount(withdrawAccount)
                    .depositAccount(depositAccount)
                    .amount(amount)
                    .gubun(TransactionEnum.valueOf(gubun))
                    .build();
        }
    }
}
