package site.metacoding.bank.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import site.metacoding.bank.config.auth.LoginUser;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.dto.ResponseDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.TransactionDepositReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.TransactionWithdrawReqDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.TransactionDepositRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.TransactionWithdrawRespDto;
import site.metacoding.bank.enums.ResponseEnum;
import site.metacoding.bank.enums.TransactionEnum;
import site.metacoding.bank.enums.UserEnum;
import site.metacoding.bank.handler.exception.CustomApiException;
import site.metacoding.bank.service.TransactionService;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class TransactionApiController {
    private final TransactionService transactionService;

    /*
     * 출금
     */
    @PostMapping("/user/{userId}/withdraw")
    public ResponseDto<?> withdraw(@PathVariable Long userId,
            @RequestBody TransactionWithdrawReqDto transactionWithdrawReqDto,
            @AuthenticationPrincipal LoginUser loginUser) {
        if (userId != loginUser.getUser().getId()) {
            if (loginUser.getUser().getRole() != UserEnum.ADMIN) {
                throw new CustomApiException(ResponseEnum.FORBIDDEN);
            }
        }
        TransactionWithdrawRespDto transactionWithdrawRespDto = transactionService.출금하기(transactionWithdrawReqDto);

        return new ResponseDto<>(ResponseEnum.POST_SUCCESS, transactionWithdrawRespDto);
    }

    /*
     * 입금
     * ATM에서 계좌로 입금하는 것이기 때문에 인증이 필요없다.
     */
    @PostMapping("/deposit")
    public ResponseDto<?> deposit(@RequestBody TransactionDepositReqDto transactionDepositReqDto) {
        TransactionDepositRespDto transactionDepositRespDto = transactionService.입금하기(transactionDepositReqDto);
        return new ResponseDto<>(ResponseEnum.POST_SUCCESS, transactionDepositRespDto);
    }

    /*
     * 이체
     */
    @PostMapping("/user/{userId}/transper")
    public ResponseDto<?> transper(@PathVariable Long userId,
            @RequestBody TransactionTransperReqDto transactionTransperReqDto,
            @AuthenticationPrincipal LoginUser loginUser) {
        if (userId != loginUser.getUser().getId()) {
            if (loginUser.getUser().getRole() != UserEnum.ADMIN) {
                throw new CustomApiException(ResponseEnum.FORBIDDEN);
            }
        }
        transactionService.이체하기(transactionTransperReqDto);
        return new ResponseDto<>(ResponseEnum.POST_SUCCESS, null);
    }

    @Getter
    @Setter
    public static class TransactionTransperReqDto {
        private Long withdrawAccountId; // 출금 계좌
        private Long depositAccountId; // 입금 계좌
        private Long amount; // 금액
        private String gubun; // 고정값 (계좌에서 계좌로 이체)

        public Transaction toEntity(Account withdrawAccount, Account depositAccount) {
            return Transaction.builder()
                    .withdrawAccount(withdrawAccount)
                    .depositAccount(depositAccount)
                    .amount(amount)
                    .gubun(TransactionEnum.valueOf(gubun))
                    .build();
        }
    }
}
