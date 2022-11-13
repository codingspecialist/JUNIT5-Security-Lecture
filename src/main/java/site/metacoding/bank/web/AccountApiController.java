package site.metacoding.bank.web;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.config.annotations.AuthorizationCheck;
import site.metacoding.bank.config.auth.LoginUser;
import site.metacoding.bank.config.enums.ResponseEnum;
import site.metacoding.bank.dto.ResponseDto;
import site.metacoding.bank.dto.account.AccountReqDto.AccountDeleteReqDto;
import site.metacoding.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountDeleteRespDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountDetailRespDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountListRespDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import site.metacoding.bank.service.AccountService;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class AccountApiController {
        private final AccountService accountService;

        @PostMapping("/account")
        public ResponseEntity<?> save(@RequestBody @Valid AccountSaveReqDto accountSaveReqDto,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal LoginUser loginUser) {
                AccountSaveRespDto accountSaveRespDto = accountService.계좌등록하기(accountSaveReqDto, loginUser.getUser());
                return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.POST_SUCCESS, accountSaveRespDto),
                                HttpStatus.CREATED);
        }

        @AuthorizationCheck
        @GetMapping("/user/{userId}/account")
        public ResponseEntity<?> list(@PathVariable Long userId, @AuthenticationPrincipal LoginUser loginUser) {
                AccountListRespDto accountListRespDto = accountService.계좌목록보기_유저별(userId);
                return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.GET_SUCCESS, accountListRespDto),
                                HttpStatus.OK);
        }

        @AuthorizationCheck
        @GetMapping("/user/{userId}/account/{accountId}")
        public ResponseEntity<?> detail(@PathVariable Long userId, @PathVariable Long accountId,
                        @AuthenticationPrincipal LoginUser loginUser) {
                AccountDetailRespDto accountDetailRespDtos = accountService.계좌상세보기(accountId);
                return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.GET_SUCCESS, accountDetailRespDtos),
                                HttpStatus.OK);
        }

        @AuthorizationCheck
        @PutMapping("/user/{userId}/account/{accountId}/delete")
        public ResponseEntity<?> delete(@PathVariable Long userId, @PathVariable Long accountId,
                        @RequestBody @Valid AccountDeleteReqDto accountDeleteReqDto, BindingResult bindingResult,
                        @AuthenticationPrincipal LoginUser loginUser) {
                AccountDeleteRespDto accountDeleteRespDto = accountService.계좌삭제(accountDeleteReqDto, accountId);
                return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.DELETE_SUCCESS, accountDeleteRespDto),
                                HttpStatus.OK);
        }

}
