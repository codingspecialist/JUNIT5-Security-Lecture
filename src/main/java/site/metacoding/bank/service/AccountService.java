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
import site.metacoding.bank.dto.AccountRespDto.AccountSaveRespDto;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public AccountSaveRespDto 계좌등록(AccountSaveReqDto accountSaveReqDto) {
        Account accountPS = accountRepository.save(accountSaveReqDto.toEntity());
        return new AccountSaveRespDto(accountPS);
    }

    @Transactional(readOnly = true)
    public List<AccountAllRespDto> 본인계좌목록(Long id) {
        return accountRepository.findByUserId(id)
                .stream()
                .map(AccountAllRespDto::new)
                .collect(Collectors.toList());
    }

}
