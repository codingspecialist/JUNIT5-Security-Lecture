package site.metacoding.bank.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.config.enums.UserEnum;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;
import site.metacoding.bank.dto.user.UserReqDto.UserJoinReqDto;
import site.metacoding.bank.dto.user.UserRespDto.UserJoinRespDto;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UserJoinRespDto 회원가입(UserJoinReqDto userJoinReqDto) {
        // ready
        String rawPassword = userJoinReqDto.getPassword();
        String encPassword = passwordEncoder.encode(rawPassword);
        userJoinReqDto.setPassword(encPassword);

        // action
        User userPS = userRepository.save(userJoinReqDto.toEntity(UserEnum.CUSTOMER));

        // cut
        return new UserJoinRespDto(userPS);
    }

}
