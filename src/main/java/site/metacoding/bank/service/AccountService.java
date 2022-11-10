package site.metacoding.bank.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.metacoding.bank.config.enums.ResponseEnum;
import site.metacoding.bank.config.exceptions.CustomApiException;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountAllRespDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountDetailRespDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountSaveRespDto;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public AccountSaveRespDto 계좌등록하기(AccountSaveReqDto accountSaveReqDto, User user) {
        Account accountPS = accountRepository.save(accountSaveReqDto.toEntity(user));
        return new AccountSaveRespDto(accountPS);
    }

    public List<AccountAllRespDto> 계좌목록보기_유저별(Long userId) {
        return accountRepository.findByUserId(userId)
                .stream()
                .map(AccountAllRespDto::new)
                .collect(Collectors.toList());
    }

    public AccountDetailRespDto 계좌상세보기(Long accountId, Long userId) {
        // 계좌확인
        Account accountPS = accountRepository.findById(accountId).orElseThrow(
                () -> new CustomApiException(ResponseEnum.BAD_REQUEST));

        // DTO
        return new AccountDetailRespDto(accountPS);
    }

    @Transactional
    public void 계좌삭제(Long accountId) {
        // 계좌확인
        accountRepository.findById(accountId).orElseThrow(
                () -> new CustomApiException(ResponseEnum.BAD_REQUEST));

        // 계좌삭제
        accountRepository.deleteById(accountId);
    }

}
