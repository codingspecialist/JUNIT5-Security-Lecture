package site.metacoding.bank.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.metacoding.bank.config.enums.ResponseEnum;
import site.metacoding.bank.dto.ResponseDto;
import site.metacoding.bank.dto.user.UserReqDto.UserJoinReqDto;
import site.metacoding.bank.dto.user.UserRespDto.UserJoinRespDto;
import site.metacoding.bank.service.UserService;

@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class UserApiController {
    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserJoinReqDto userJoinReqDto) {
        UserJoinRespDto userJoinRespDto = userService.회원가입(userJoinReqDto);
        return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.JOIN_SUCCESS, userJoinRespDto),
                HttpStatus.CREATED);
    }

}
