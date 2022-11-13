package site.metacoding.bank.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import site.metacoding.bank.dto.transaction.TransactionRespDto.TransactionListRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.TransferRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.WithdrawRespDto;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TransactionService {
        private final Logger log = LoggerFactory.getLogger(getClass());
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
                                .orElseThrow(() -> new CustomApiException(ResponseEnum.BAD_REQUEST));

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
                Transaction transactionPS = transactionRepository.save(transaction);

                // DTO
                return new WithdrawRespDto(withdrawAccountPS, transactionPS);
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
                Transaction transaction = withdrawAccountPS.transper(transferReqDto.getAmount(), depositAccountPS);
                Transaction transactionPS = transactionRepository.save(transaction);

                // DTO
                return new TransferRespDto(withdrawAccountPS, transactionPS);
        }

        // 계좌상세보기할 때 전체 계좌목록 나옴
        // 입금만 보기, 출금만 보기, 전체보기할 때 Account, User 정보 없이 순수 Transaction 내용만 불러올때 동적쿼리 사용
        public TransactionListRespDto 입출금목록보기(Long userId, Long accountId, String gubun, Integer page) {
                // 계좌 확인
                Account accountPS = accountRepository.findById(accountId)
                                .orElseThrow(() -> new CustomApiException(ResponseEnum.BAD_REQUEST));

                // 계좌 소유자 확인
                accountPS.ownerCheck(userId);

                // 입출금 내역 조회
                List<Transaction> transactionListPS = transactionRepository.findByAccountId(accountId, gubun, page);

                // DTO (동적 쿼리)
                return new TransactionListRespDto(transactionListPS);
        }
}
