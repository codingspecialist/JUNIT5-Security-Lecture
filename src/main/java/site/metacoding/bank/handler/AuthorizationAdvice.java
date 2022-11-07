package site.metacoding.bank.handler;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import site.metacoding.bank.config.auth.LoginUser;
import site.metacoding.bank.config.enums.ResponseEnum;
import site.metacoding.bank.config.enums.UserEnum;
import site.metacoding.bank.config.exceptions.CustomApiException;

@Slf4j
@Component
@Aspect
public class AuthorizationAdvice {

    @Pointcut("@annotation(site.metacoding.bank.config.annotations.AuthorizationCheck)")
    public void authorizationCheck() {
    }

    @Around("authorizationCheck()")
    public Object apiAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 메서드 파라메터 변수명들
        String[] paramNames = ((CodeSignature) proceedingJoinPoint.getSignature()).getParameterNames();
        // 메서드 파라메터 변수값들
        Object[] paramValues = proceedingJoinPoint.getArgs();
        Long userId = null;
        LoginUser loginUser = null;

        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals("userId")) {
                userId = (Long) paramValues[i];
            }
            if (paramNames[i].equals("loginUser")) {
                loginUser = (LoginUser) paramValues[i];
            }
        }

        if (userId == null || loginUser == null) {
            throw new CustomApiException(ResponseEnum.UNAUTHORIZED);
        }

        // 권한 확인
        if (userId != loginUser.getUser().getId()) {
            if (loginUser.getUser().getRole() != UserEnum.ADMIN) {
                throw new CustomApiException(ResponseEnum.FORBIDDEN);
            }
        }

        return proceedingJoinPoint.proceed();
    }

}
