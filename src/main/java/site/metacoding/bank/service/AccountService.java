package site.metacoding.bank.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.config.enums.ResponseEnum;
import site.metacoding.bank.config.exceptions.CustomApiException;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.dto.account.AccountReqDto.AccountDeleteReqDto;
import site.metacoding.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountAllRespDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountDeleteRespDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountDetailRespDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountSaveRespDto;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public AccountSaveRespDto 계좌등록하기(AccountSaveReqDto accountSaveReqDto, User user) {
        Account accountPS = accountRepository.save(accountSaveReqDto.toEntity(user));
        return new AccountSaveRespDto(accountPS);
    }

    public List<AccountAllRespDto> 계좌목록보기_유저별(Long userId) {
        return accountRepository.findByActiveUserId(userId)
                .stream()
                .map(AccountAllRespDto::new)
                .collect(Collectors.toList());
    }

    public AccountDetailRespDto 계좌상세보기(Long accountId) {
        // 계좌확인 (삭제된 계좌인지 확인)
        Account accountPS = accountRepository.findById(accountId).orElseThrow(
                () -> new CustomApiException(ResponseEnum.BAD_REQUEST));

        // 계좌 입출금이체 내역
        List<Transaction> transactions = transactionRepository.findByTransactionHistory(accountPS.getId(), null);

        // DTO
        return new AccountDetailRespDto(accountPS, transactions);
    }

    @Transactional
    public AccountDeleteRespDto 계좌삭제(AccountDeleteReqDto accountDeleteReqDto, Long accountId) {

        // 계좌확인
        Account accountPS = accountRepository.findById(accountId).orElseThrow(
                () -> new CustomApiException(ResponseEnum.BAD_REQUEST));

        // 계좌 비밀번호 확인
        accountPS.passwordCheck(accountDeleteReqDto.getAccountPassword());

        // 계좌삭제
        accountPS.delete();

        // DTO
        return new AccountDeleteRespDto(accountPS);
    }

}
