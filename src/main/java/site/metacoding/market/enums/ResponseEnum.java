package site.metacoding.market.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseEnum {
    BAD_REQUEST(400, "잘못된 요청"),
    UNAUTHORIZED(401, "인증안됨"),
    FORBIDDEN(403, "권한없음"),
    LOGIN_SUCCESS(200, "로그인 성공"),
    LOGIN_FAIL(500, "로그인 실패"),
    JOIN_SUCCESS(201, "회원가입 성공"),
    JOIN_FAIL(500, "회원가입 실패"),
    GET_SUCCESS(200, "조회 성공"),
    GET_FAIL(400, "조회 실패"),
    POST_SUCCESS(200, "등록 성공"),
    POST_FAIL(404, "등록 실패"),
    PUT_SUCCESS(200, "수정 성공"),
    PUT_FAIL(400, "수정 실패"),
    DELETE_SUCCESS(200, "삭제 성공"),
    DELETE_FAIL(200, "삭제 실패");

    private final int code;
    private final String message;

}