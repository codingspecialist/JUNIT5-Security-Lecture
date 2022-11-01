package site.metacoding.bank.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.dto.AccountReqDto.AccountSaveReqDto;
import site.metacoding.bank.dto.AccountRespDto.AccountAllRespDto;
import site.metacoding.bank.dto.AccountRespDto.AccountDetailRespDto;
import site.metacoding.bank.dto.AccountRespDto.AccountSaveRespDto;
import site.metacoding.bank.enums.ResponseEnum;
import site.metacoding.bank.handler.exception.CustomApiException;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public AccountSaveRespDto 계좌등록하기(AccountSaveReqDto accountSaveReqDto) {
        Account accountPS = accountRepository.save(accountSaveReqDto.toEntity());
        return new AccountSaveRespDto(accountPS);
    }

    public List<AccountAllRespDto> 계좌목록보기_유저별(Long id) {
        return accountRepository.findByUserId(id)
                .stream()
                .map(AccountAllRespDto::new)
                .collect(Collectors.toList());
    }

    public AccountDetailRespDto 계좌상세보기(Long accountId, Long userId) {
        // 계좌확인
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new CustomApiException(ResponseEnum.BAD_REQUEST));

        // DTO
        return new AccountDetailRespDto(account);
    }

    @Transactional
    public void 계좌삭제(Long accountId) {
        // 계좌확인
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new CustomApiException(ResponseEnum.BAD_REQUEST));

        // 계좌삭제
        accountRepository.deleteById(accountId);
    }

}
