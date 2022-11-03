package site.metacoding.bank.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.metacoding.bank.config.auth.LoginUser;
import site.metacoding.bank.dto.ResponseDto;
import site.metacoding.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountAllRespDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountDetailRespDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import site.metacoding.bank.enums.ResponseEnum;
import site.metacoding.bank.enums.UserEnum;
import site.metacoding.bank.handler.exception.CustomApiException;
import site.metacoding.bank.service.AccountService;

@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class AccountApiController {
    private final AccountService accountService;

    @PostMapping("/account")
    public ResponseEntity<?> save(@RequestBody AccountSaveReqDto accountSaveReqDto,
            @AuthenticationPrincipal LoginUser loginUser) {
        accountSaveReqDto.setLoginUser(loginUser);
        AccountSaveRespDto accountSaveRespDto = accountService.계좌등록하기(accountSaveReqDto);
        return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.POST_SUCCESS, accountSaveRespDto),
                HttpStatus.CREATED);
    }

    @GetMapping("user/{userId}/account")
    public ResponseEntity<?> list(@PathVariable Long userId, @AuthenticationPrincipal LoginUser loginUser) {
        if (userId != loginUser.getUser().getId()) {
            if (loginUser.getUser().getRole() != UserEnum.ADMIN) {
                throw new CustomApiException(ResponseEnum.FORBIDDEN);
            }
        }

        List<AccountAllRespDto> accountAllRespDtos = accountService.계좌목록보기_유저별(userId);
        return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.GET_SUCCESS, accountAllRespDtos),
                HttpStatus.OK);
    }

    @GetMapping("user/{userId}/account/{accountId}")
    public ResponseEntity<?> detail(@PathVariable Long userId, @PathVariable Long accountId,
            @AuthenticationPrincipal LoginUser loginUser) {
        if (userId != loginUser.getUser().getId()) {
            if (loginUser.getUser().getRole() != UserEnum.ADMIN) {
                throw new CustomApiException(ResponseEnum.FORBIDDEN);
            }
        }
        AccountDetailRespDto accountDetailRespDtos = accountService.계좌상세보기(accountId, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.GET_SUCCESS, accountDetailRespDtos),
                HttpStatus.OK);
    }

    @DeleteMapping("/user/{userId}/account/{accountId}")
    public ResponseEntity<?> delete(@PathVariable Long userId, @PathVariable Long accountId,
            @AuthenticationPrincipal LoginUser loginUser) {
        if (userId != loginUser.getUser().getId()) {
            if (loginUser.getUser().getRole() != UserEnum.ADMIN) {
                throw new CustomApiException(ResponseEnum.FORBIDDEN);
            }
        }
        accountService.계좌삭제(accountId);
        return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.DELETE_SUCCESS, null),
                HttpStatus.OK);
    }

}
