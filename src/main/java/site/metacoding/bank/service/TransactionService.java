package site.metacoding.bank.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.metacoding.bank.config.enums.ResponseEnum;
import site.metacoding.bank.config.enums.TransactionEnum;
import site.metacoding.bank.config.exceptions.CustomApiException;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
import site.metacoding.bank.dto.transaction.TransactionReqDto.DepositReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.TransperReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.WithdrawReqDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.DepositRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.TransactionHistoryRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.TransperRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.WithdrawRespDto;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TransactionService {
        private final TransactionRepository transactionRepository;
        private final AccountRepository accountRepository;

        @Transactional
        public DepositRespDto 입금하기(DepositReqDto depositReqDto) {
                // GUBUN값 검증
                if (TransactionEnum.valueOf(depositReqDto.getGubun()) != TransactionEnum.DEPOSIT) {
                        throw new CustomApiException(ResponseEnum.BAD_REQUEST);
                }

                // 입금계좌 확인
                Account depositAccountPS = accountRepository.findById(depositReqDto.getDepositAccountId())
                                .orElseThrow(
                                                () -> new CustomApiException(ResponseEnum.BAD_REQUEST));

                // 입금 하기
                Transaction depositPS = transactionRepository
                                .save(depositReqDto.toEntity(depositAccountPS));
                depositAccountPS.deposit(depositPS);

                // DTO
                return new DepositRespDto(depositPS);
        }

        @Transactional
        public WithdrawRespDto 출금하기(WithdrawReqDto withdrawReqDto, Long userId) {
                // GUBUN값 검증
                if (TransactionEnum.valueOf(withdrawReqDto.getGubun()) != TransactionEnum.WITHDRAW) {
                        throw new CustomApiException(ResponseEnum.BAD_REQUEST);
                }

                // 출금계좌 확인
                Account withdrawAccountPS = accountRepository.findById(withdrawReqDto.getWithdrawAccountId())
                                .orElseThrow(() -> new CustomApiException(ResponseEnum.BAD_REQUEST));

                // 출금계좌 소유자 확인
                withdrawAccountPS.isAccountOwner(userId);

                // 출금 하기
                Transaction withdrawPS = transactionRepository
                                .save(withdrawReqDto.toEntity(withdrawAccountPS));
                withdrawAccountPS.withdraw(withdrawPS);

                // DTO
                return new WithdrawRespDto(withdrawPS);
        }

        @Transactional
        public TransperRespDto 이체하기(TransperReqDto transperReqDto, Long userId) {
                // GUBUN값 검증
                if (TransactionEnum.valueOf(transperReqDto.getGubun()) != TransactionEnum.TRANSPER) {
                        throw new CustomApiException(ResponseEnum.BAD_REQUEST);
                }

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

                // 이체 하기
                Transaction transperPS = transactionRepository
                                .save(transperReqDto.toEntity(withdrawAccountPS, depositAccountPS));
                withdrawAccountPS.withdraw(transperPS);
                depositAccountPS.deposit(transperPS);

                // DTO
                return new TransperRespDto(transperPS);
        }

        public TransactionHistoryRespDto 입출금목록보기(Long userId, Long accountId, String gubun) {
                // 계좌 확인
                Account accountPS = accountRepository.findById(accountId)
                                .orElseThrow(() -> new CustomApiException(ResponseEnum.BAD_REQUEST));

                // 계좌 소유자 확인
                accountPS.isAccountOwner(userId);

                // 입출금 내역 조회
                List<Transaction> transactionListPS = transactionRepository
                                .findByTransactionHistory(accountId, gubun);

                // DTO (Collection Lazy Loading)
                TransactionHistoryRespDto transactionHistoryRespDto = new TransactionHistoryRespDto(accountPS,
                                transactionListPS);
                return transactionHistoryRespDto;
        }

        // public WithdrawHistoryRespDto 출금목록보기(Long userId, Long accountId) {
        // // 계좌 확인
        // Account accountPS = accountRepository.findById(accountId)
        // .orElseThrow(() -> new CustomApiException(ResponseEnum.BAD_REQUEST));

        // // 계좌 소유자 확인
        // accountPS.isAccountOwner(userId);

        // // DTO (Collection Lazy Loading)
        // WithdrawHistoryRespDto withdrawHistoryRespDto = new
        // WithdrawHistoryRespDto(accountPS);
        // return withdrawHistoryRespDto;
        // }

        // public DepositHistoryRespDto 입금목록보기(Long userId, Long accountId) {
        // // 계좌 확인
        // Account accountPS = accountRepository.findById(accountId)
        // .orElseThrow(() -> new CustomApiException(ResponseEnum.BAD_REQUEST));

        // // 계좌 소유자 확인
        // accountPS.isAccountOwner(userId);
        // DepositHistoryRespDto depositHistoryRespDto = new
        // DepositHistoryRespDto(accountPS);
        // return depositHistoryRespDto;
        // }

}
