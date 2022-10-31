package site.metacoding.bank.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.domain.account.AccountRepository;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
}
