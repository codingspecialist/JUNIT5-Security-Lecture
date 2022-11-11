package site.metacoding.bank.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import site.metacoding.bank.config.annotations.AuthorizationCheck;
import site.metacoding.bank.config.auth.LoginUser;
import site.metacoding.bank.config.enums.ResponseEnum;
import site.metacoding.bank.dto.ResponseDto;
import site.metacoding.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountAllRespDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountDetailRespDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import site.metacoding.bank.service.AccountService;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class AccountApiController {
        private final AccountService accountService;

        @PostMapping("/account")
        public ResponseEntity<?> save(@RequestBody AccountSaveReqDto accountSaveReqDto,
                        @AuthenticationPrincipal LoginUser loginUser) {
                AccountSaveRespDto accountSaveRespDto = accountService.계좌등록하기(accountSaveReqDto, loginUser.getUser());
                return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.POST_SUCCESS, accountSaveRespDto),
                                HttpStatus.CREATED);
        }

        @AuthorizationCheck
        @GetMapping("/user/{userId}/account")
        public ResponseEntity<?> list(@PathVariable Long userId, @AuthenticationPrincipal LoginUser loginUser) {
                List<AccountAllRespDto> accountAllRespDtos = accountService.계좌목록보기_유저별(userId);
                return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.GET_SUCCESS, accountAllRespDtos),
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
        @DeleteMapping("/user/{userId}/account/{accountId}")
        public ResponseEntity<?> delete(@PathVariable Long userId, @PathVariable Long accountId,
                        @AuthenticationPrincipal LoginUser loginUser) {
                accountService.계좌삭제(accountId);
                return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.DELETE_SUCCESS, null),
                                HttpStatus.OK);
        }

}
