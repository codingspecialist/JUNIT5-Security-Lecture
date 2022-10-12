package site.metacoding.market.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.metacoding.market.enums.ResponseEnum;
import site.metacoding.market.web.dto.ResponseDto;

@Component
public class LoginHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        makeResponseData(response, ResponseEnum.LOGIN_FAIL);

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        makeResponseData(response, ResponseEnum.LOGIN_SUCCESS);
    }

    private void makeResponseData(HttpServletResponse response, ResponseEnum responseEnum) throws IOException {
        ObjectMapper om = new ObjectMapper();
        ResponseDto<?> responseDto = new ResponseDto<>(responseEnum);
        String responseBody = om.writer().writeValueAsString(responseDto);
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().println(responseBody);
        response.setStatus(responseDto.getCode());

    }

}
