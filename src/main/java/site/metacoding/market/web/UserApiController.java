package site.metacoding.market.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.metacoding.market.dto.ResponseDto;
import site.metacoding.market.dto.UserReqDto.UserJoinReqDto;
import site.metacoding.market.enums.ResponseEnum;
import site.metacoding.market.service.UserService;

@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class UserApiController {
    private final UserService userService;

    @PostMapping("/v1/join")
    public ResponseEntity<?> join(@RequestBody UserJoinReqDto userJoinReqDto) {
        return new ResponseEntity<>(new ResponseDto<>(ResponseEnum.JOIN_SUCCESS, userService.회원가입(userJoinReqDto)),
                HttpStatus.CREATED);
    }

}
