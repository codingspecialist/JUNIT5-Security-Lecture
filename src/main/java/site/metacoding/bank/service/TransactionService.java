package site.metacoding.bank.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
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

                // 출금하기
                Transaction withdrawPS = transactionRepository
                                .save(withdrawReqDto.toEntity(withdrawAccountPS));

                // 계좌잔액 수정
                withdrawAccountPS.withdraw(withdrawReqDto.getAmount());

                // DTO
                return new WithdrawRespDto(withdrawPS);
        }

        @Transactional
        public DepositRespDto 입금하기(DepositReqDto depositReqDto) {
                // 입금계좌 확인
                Account depositAccountPS = accountRepository.findById(depositReqDto.getDepositAccountId())
                                .orElseThrow(
                                                () -> new CustomApiException(ResponseEnum.BAD_REQUEST));

                // 입금하기
                Transaction depositPS = transactionRepository
                                .save(depositReqDto.toEntity(depositAccountPS));

                // 입금 계좌잔액 수정
                depositAccountPS.deposit(depositPS.getAmount());

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

                // 이체하기
                Transaction transperPS = transactionRepository
                                .save(transperReqDto.toEntity(withdrawAccountPS, depositAccountPS));

                // DTO
                return new TransperRespDto(transperPS);
        }

        public void 출금목록보기(Long withdrawAccountId) {
        }

}
