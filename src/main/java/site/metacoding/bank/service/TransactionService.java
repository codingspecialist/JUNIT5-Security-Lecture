package site.metacoding.bank.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.domain.transaction.TransactionRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
}
