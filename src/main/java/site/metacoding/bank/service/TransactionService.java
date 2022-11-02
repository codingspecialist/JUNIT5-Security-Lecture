package site.metacoding.bank.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.dto.transaction.TransactionReqDto.DepositReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.TransperReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.WithdrawReqDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.DepositRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.TransperRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.WithdrawRespDto;
import site.metacoding.bank.enums.ResponseEnum;
import site.metacoding.bank.handler.exception.CustomApiException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TransactionService {
        private final TransactionRepository transactionRepository;
        private final AccountRepository accountRepository;

        @Transactional
        public WithdrawRespDto 출금하기(WithdrawReqDto withdrawReqDto, Long userId) {
                // 출금계좌 확인
                Account withdrawAccountPS = accountRepository.findById(withdrawReqDto.getWithdrawAccountId())
                                .orElseThrow(() -> new CustomApiException(ResponseEnum.BAD_REQUEST));

                // 출금계좌 소유자 확인
                withdrawAccountPS.isAccountOwner(userId);

                // 출금 계좌잔액 수정
                withdrawAccountPS.withdraw(withdrawReqDto.getAmount());

                // 출금 이력 남기기
                Transaction withdrawPS = transactionRepository
                                .save(withdrawReqDto.toEntity(withdrawAccountPS));

                // DTO
                return new WithdrawRespDto(withdrawPS);
        }

        @Transactional
        public DepositRespDto 입금하기(DepositReqDto depositReqDto) {
                // 입금계좌 확인
                Account depositAccountPS = accountRepository.findById(depositReqDto.getDepositAccountId())
                                .orElseThrow(
                                                () -> new CustomApiException(ResponseEnum.BAD_REQUEST));

                // 입금 계좌잔액 수정
                depositAccountPS.deposit(depositReqDto.getAmount());

                // 입금 이력 남기기
                Transaction depositPS = transactionRepository
                                .save(depositReqDto.toEntity(depositAccountPS));

                // DTO
                return new DepositRespDto(depositPS);
        }

        @Transactional
        public TransperRespDto 이체하기(TransperReqDto transperReqDto, Long userId) {
                // 입금 계좌와 출금 계좌가 동일하면 거부
                if (transperReqDto.getWithdrawAccountId() == transperReqDto.getDepositAccountId()) {
                        throw new CustomApiException(ResponseEnum.SAME_ACCOUNT);
                }

                // 출금계좌 확인
                Account withdrawAccountPS = accountRepository.findById(transperReqDto.getWithdrawAccountId())
                                .orElseThrow(() -> new CustomApiException(ResponseEnum.BAD_REQUEST));

                // 출금계좌 소유자 확인
                withdrawAccountPS.isAccountOwner(userId);

                // 입금계좌 확인
                Account depositAccountPS = accountRepository.findById(transperReqDto.getDepositAccountId())
                                .orElseThrow(() -> new CustomApiException(ResponseEnum.BAD_REQUEST));

                // 출금 계좌잔액 수정
                withdrawAccountPS.withdraw(transperReqDto.getAmount());

                // 입급 계좌잔액 수정
                depositAccountPS.deposit(transperReqDto.getAmount());

                // 이체 이력 남기기
                Transaction transperPS = transactionRepository
                                .save(transperReqDto.toEntity(withdrawAccountPS, depositAccountPS));

                // DTO
                return new TransperRespDto(transperPS);
        }

        public void 출금목록보기(Long userId, Long accountId) {
                // 계좌 확인
                Account accountPS = accountRepository.findById(accountId)
                                .orElseThrow(() -> new CustomApiException(ResponseEnum.BAD_REQUEST));

                // 계좌 소유자 확인
                accountPS.isAccountOwner(userId);

                // WithDrawAccount (Lazy Loading)

        }

        @Getter
        @Setter
        public static class WithdrawAllRespDto {
                private Long id;
                private Long amount;
                private String gubun;
                private WithdrawAccountDto withdrawAccount;

                public WithdrawAllRespDto(Transaction transaction) {
                        this.id = transaction.getId();
                        this.amount = transaction.getAmount();
                        this.gubun = transaction.getGubun().name();
                        this.withdrawAccount = new WithdrawAccountDto(transaction.getWithdrawAccount());
                }

                @Getter
                @Setter
                public class WithdrawAccountDto {
                        private Long id;
                        private Long number;
                        private Long balance;
                        private UserDto user;

                        public WithdrawAccountDto(Account account) {
                                this.id = account.getId();
                                this.number = account.getNumber();
                                this.balance = account.getBalance();
                                this.user = new UserDto(account.getUser());
                        }

                        @Getter
                        @Setter
                        public class UserDto {
                                private Long id;
                                private String username;

                                public UserDto(User user) {
                                        this.id = user.getId();
                                        this.username = user.getUsername();
                                }

                        }
                }
        }

}
