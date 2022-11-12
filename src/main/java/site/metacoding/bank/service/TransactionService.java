package site.metacoding.bank.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.config.enums.ResponseEnum;
import site.metacoding.bank.config.enums.TransactionEnum;
import site.metacoding.bank.config.exceptions.CustomApiException;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
import site.metacoding.bank.dto.transaction.TransactionReqDto.DepositReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.TransferReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.WithdrawReqDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.DepositRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.TransactionHistoryRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.TransferRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.WithdrawRespDto;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TransactionService {
        private final TransactionRepository transactionRepository;
        private final AccountRepository accountRepository;
        private final EntityManager em;

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

                // 0원 체크
                depositAccountPS.zeroAmountCheck(depositReqDto.getAmount());

                // 입금 하기
                Transaction transaction = depositAccountPS.deposit(depositReqDto.getAmount());
                Transaction transactionPS = transactionRepository.save(transaction);

                // DTO
                return new DepositRespDto(depositAccountPS, transactionPS);
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

                // 0원 체크
                withdrawAccountPS.zeroAmountCheck(withdrawReqDto.getAmount());

                // 출금계좌 소유자 확인
                withdrawAccountPS.ownerCheck(userId);

                // 출금계좌 비밀번호 확인
                withdrawAccountPS.passwordCheck(withdrawReqDto.getAccountPassword());

                // 출금 하기
                Transaction transaction = withdrawAccountPS.withdraw(withdrawReqDto.getAmount());
                em.persist(withdrawAccountPS);

                // DTO
                return new WithdrawRespDto(withdrawAccountPS, transaction);
        }

        @Transactional
        public TransferRespDto 이체하기(TransferReqDto transferReqDto, Long userId) {
                // GUBUN값 검증
                if (TransactionEnum.valueOf(transferReqDto.getGubun()) != TransactionEnum.TRANSFER) {
                        throw new CustomApiException(ResponseEnum.BAD_REQUEST);
                }

                // 입금 계좌와 출금 계좌가 동일하면 거부
                if (transferReqDto.getWithdrawAccountId() == transferReqDto.getDepositAccountId()) {
                        throw new CustomApiException(ResponseEnum.SAME_ACCOUNT);
                }

                // 출금계좌 확인
                Account withdrawAccountPS = accountRepository.findById(transferReqDto.getWithdrawAccountId())
                                .orElseThrow(() -> new CustomApiException(ResponseEnum.BAD_REQUEST));

                // 0원 체크
                withdrawAccountPS.zeroAmountCheck(transferReqDto.getAmount());

                // 출금계좌 소유자 확인
                withdrawAccountPS.ownerCheck(userId);

                // 출금계좌 비밀번호 확인
                withdrawAccountPS.passwordCheck(transferReqDto.getAccountPassword());

                // 입금계좌 확인
                Account depositAccountPS = accountRepository.findById(transferReqDto.getDepositAccountId())
                                .orElseThrow(() -> new CustomApiException(ResponseEnum.BAD_REQUEST));

                // 이체 하기
                Transaction transferPS = transactionRepository
                                .save(transferReqDto.toEntity(withdrawAccountPS, depositAccountPS));
                withdrawAccountPS.withdraw(transferPS.getAmount());
                depositAccountPS.deposit(transferPS.getAmount());

                // DTO
                return new TransferRespDto(transferPS);
        }

        public TransactionHistoryRespDto 입출금목록보기(Long userId, Long accountId, String gubun) {
                // 계좌 확인
                Account accountPS = accountRepository.findById(accountId)
                                .orElseThrow(() -> new CustomApiException(ResponseEnum.BAD_REQUEST));

                // 계좌 소유자 확인
                accountPS.ownerCheck(userId);

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
