package site.metacoding.bank.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.config.auth.LoginUser;
import site.metacoding.bank.dto.ResponseDto;
import site.metacoding.bank.dto.TransactionReqDto.TransactionDepositReqDto;
import site.metacoding.bank.dto.TransactionReqDto.TransactionWithdrawReqDto;
import site.metacoding.bank.dto.TransactionRespDto.TransactionDepositRespDto;
import site.metacoding.bank.dto.TransactionRespDto.TransactionWithdrawRespDto;
import site.metacoding.bank.enums.ResponseEnum;
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

}
