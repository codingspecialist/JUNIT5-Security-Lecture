package site.metacoding.bank.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
import site.metacoding.bank.dto.TransactionReqDto.TransactionWithdrawReqDto;
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
        Account accountPS = accountRepository.findById(transactionWithdrawReqDto.getWithdrawAccountId())
                .orElseThrow(() -> new CustomApiException(ResponseEnum.BAD_REQUEST));
        // 출금하기
        Transaction transactionWithdrawPS = transactionRepository.save(transactionWithdrawReqDto.toEntity(accountPS));

        // 계좌 잔액 수정
        Account account = accountRepository.findById(transactionWithdrawReqDto.getWithdrawAccountId()).orElseThrow(
                () -> new CustomApiException(ResponseEnum.BAD_REQUEST));
        account.withdraw(transactionWithdrawReqDto.getAmount());

        // DTO
        return new TransactionWithdrawRespDto(transactionWithdrawPS);
    }

}
