package site.metacoding.bank.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.metacoding.bank.config.auth.LoginUser;
import site.metacoding.bank.dto.ResponseDto;
import site.metacoding.bank.dto.user.UserRespDto.UserLoginRespDto;
import site.metacoding.bank.enums.ResponseEnum;

@Component
public class LoginHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        makeResponseData(response, ResponseEnum.LOGIN_FAIL, null);

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserLoginRespDto UserLoginRespDto = new UserLoginRespDto(loginUser.getUser());
        makeResponseData(response, ResponseEnum.LOGIN_SUCCESS, UserLoginRespDto);
    }

    private <T> void makeResponseData(HttpServletResponse response, ResponseEnum responseEnum, T data)
            throws IOException {
        ObjectMapper om = new ObjectMapper();
        ResponseDto<?> responseDto = new ResponseDto<>(responseEnum, data);
        String responseBody = om.writer().writeValueAsString(responseDto);
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(responseDto.getCode());
        response.getWriter().println(responseBody);
    }

}
