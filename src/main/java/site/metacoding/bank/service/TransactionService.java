package site.metacoding.bank.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
import site.metacoding.bank.dto.TransactionReqDto.TransactionDepositReqDto;
import site.metacoding.bank.dto.TransactionReqDto.TransactionWithdrawReqDto;
import site.metacoding.bank.dto.TransactionRespDto.TransactionDepositRespDto;
import site.metacoding.bank.dto.TransactionRespDto.TransactionWithdrawRespDto;
import site.metacoding.bank.enums.ResponseEnum;
import site.metacoding.bank.handler.exception.CustomApiException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public TransactionWithdrawRespDto 출금하기(TransactionWithdrawReqDto transactionWithdrawReqDto) {
        // 출금계좌 확인
        Account withdrawAccountPS = accountRepository.findById(transactionWithdrawReqDto.getWithdrawAccountId())
                .orElseThrow(() -> new CustomApiException(ResponseEnum.BAD_REQUEST));
        // 출금하기
        Transaction transactionWithdrawPS = transactionRepository
                .save(transactionWithdrawReqDto.toEntity(withdrawAccountPS));

        // 계좌잔액 수정
        withdrawAccountPS.withdraw(transactionWithdrawReqDto.getAmount());

        // DTO
        return new TransactionWithdrawRespDto(transactionWithdrawPS);
    }

    @Transactional
    public TransactionDepositRespDto 입금하기(TransactionDepositReqDto transactionDepositReqDto) {
        // 입금계좌 확인
        Account depositAccountPS = accountRepository.findById(transactionDepositReqDto.getDepositAccountId())
                .orElseThrow(
                        () -> new CustomApiException(ResponseEnum.BAD_REQUEST));

        // 입금하기
        Transaction transactionDepositPS = transactionRepository
                .save(transactionDepositReqDto.toEntity(depositAccountPS));

        // 계좌잔액 수정
        depositAccountPS.deposit(transactionDepositPS.getAmount());

        // DTO
        return new TransactionDepositRespDto(transactionDepositPS);
    }

}
