package site.metacoding.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.extern.slf4j.Slf4j;
import site.metacoding.bank.config.enums.UserEnum;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;
import site.metacoding.bank.dto.user.UserReqDto.UserJoinReqDto;
import site.metacoding.bank.dto.user.UserRespDto.UserJoinRespDto;

/*
 * Mock -> 진짜 객체를 추상화된 가짜 객체로 만들어서 Mockito환경에 주입함.
 * InjectMocks -> Mock된 가짜 객체를 진짜 객체 UserService를 만들어서 주입함
 * MockBean -> Mock객체들을 스프링 ApplicationContext에 주입함. (IoC컨테이너 주입)
 * Spy -> 진짜 객체를 만들어서 Mockito환경에 주입함.
 * SpyBean -> Spay객체들을 스프링 ApplicationContext에 주입함. (IoC컨테이너 주입)
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void 회원가입_test() throws Exception {
        // given
        UserJoinReqDto userJoinReqDto = new UserJoinReqDto();
        userJoinReqDto.setUsername("ssar");
        userJoinReqDto.setPassword("1234");
        userJoinReqDto.setEmail("ssar@nate.com");

        // stub (가정)
        String encPassword = passwordEncoder.encode("1234");
        log.debug("디버그 : " + encPassword); // @Spy 안쓰면 null 이 리턴됨.
        User userPS = User.builder()
                .id(1L)
                .username("ssar")
                .password(encPassword)
                .email("ssar@nate.com")
                .role(UserEnum.CUSTOMER)
                .build();
        when(userRepository.save(any())).thenReturn(userPS);

        // when
        UserJoinRespDto userJoinRespDto = userService.회원가입(userJoinReqDto);

        // then
        assertThat(userJoinRespDto.getId()).isEqualTo(1L);
        assertThat(userJoinRespDto.getUsername()).isEqualTo("ssar");
    }
}
