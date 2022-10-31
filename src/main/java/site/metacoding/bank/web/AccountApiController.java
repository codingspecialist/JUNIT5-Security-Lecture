package site.metacoding.bank.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.metacoding.bank.config.auth.LoginUser;
import site.metacoding.bank.dto.AccountReqDto.AccountSaveReqDto;
import site.metacoding.bank.dto.AccountRespDto.AccountSaveRespDto;
import site.metacoding.bank.dto.ResponseDto;
import site.metacoding.bank.enums.ResponseEnum;
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
        log.debug("디버그 : 111111");
        accountSaveReqDto.setLoginUser(loginUser);
        AccountSaveRespDto accountSaveRespDto = accountService.계좌등록(accountSaveReqDto);
        log.debug("디버그 : 22222222");
        return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.POST_SUCCESS, accountSaveRespDto),
                HttpStatus.CREATED);
    }

}
