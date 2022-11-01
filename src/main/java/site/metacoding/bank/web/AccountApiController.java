package site.metacoding.bank.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.metacoding.bank.config.auth.LoginUser;
import site.metacoding.bank.dto.AccountReqDto.AccountSaveReqDto;
import site.metacoding.bank.dto.AccountRespDto.AccountAllRespDto;
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
        accountSaveReqDto.setLoginUser(loginUser);
        AccountSaveRespDto accountSaveRespDto = accountService.계좌등록(accountSaveReqDto);
        return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.POST_SUCCESS, accountSaveRespDto),
                HttpStatus.CREATED);
    }

    @GetMapping("/account")
    public ResponseDto<?> list(@AuthenticationPrincipal LoginUser loginUser) {
        List<AccountAllRespDto> accountAllRespDtos = accountService.본인계좌목록(loginUser.getUser().getId());
        return new ResponseDto<>(ResponseEnum.GET_SUCCESS, accountAllRespDtos);
    }
}
