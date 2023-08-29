package com.now.common.exception;

import lombok.Getter;

/**
 * API 예외 타입을 정의한 enum
 */
@Getter
public enum ErrorType {

    NOT_AUTHENTICATED(1001, "인증되지 않았습니다."),
    ALREADY_AUTHENTICATED(1002, "이미 인증정보가 존재합니다."),
    INVALID_TOKEN(1003, "유효하지 않은 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(1004, "유효기간이 만료된 액세스 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(1005, "유효기간이 만료된 리프레시 토큰입니다."),
    FORBIDDEN(1006, "권한이 없습니다."),
    LOGGED_OUT_TOKEN(1007, "로그아웃된 토큰입니다."),
    NOT_FOUND_TOKEN(1008, "토큰 정보가 존재하지 않습니다."),

    NOT_FOUND_MEMBER(2001, "존재하지 않는 회원입니다."),
    DUPLICATE_MEMBER_INFO_ID(2002, "중복된 회원 아이디입니다."),
    DUPLICATE_MEMBER_INFO_NICKNAME(2003, "중복된 회원 닉네임입니다."),
    DUPLICATE_MEMBER_INFO_ID_AND_NICKNAME(2004, "중복된 회원 아이디와 닉네임입니다."),

    NOT_FOUND_MANAGER(3001, "존재하지 않는 매니저입니다."),

    NOT_FOUND_CATEGORY(4001, "존재하지 않는 카테고리입니다."),
    INVALID_CATEGORY(4002, "허용되지 않는 카테고리입니다."),

    NOT_FOUND_POST(6001, "게시글을 찾을 수 없습니다."),
    CAN_NOT_CREATE_POST(6002, "게시글을 생성할 수 없습니다."),
    CAN_NOT_UPDATE_POST(6003, "게시글을 수정할 수 없습니다."),
    CAN_NOT_UPDATE_OTHER_MEMBER_POST(6004, "다른 회원이 작성한 게시글을 수정할 수 없습니다."),
    CAN_NOT_DELETE_OTHER_MEMBER_POST(6005, "다른 회원이 작성한 게시글을 삭제할 수 없습니다."),
    CAN_NOT_VIEW_SECRET_INQUIRY(6006, "비밀글로 설정된 문의글을 볼 수 없습니다."),
    CAN_NOT_DELETE_POST_WITH_OTHER_MEMBER_COMMENTS(6007, "다른 회원이 작성한 댓글이 있으므로 해당 게시글을 삭제할 수 없습니다."),
    CAN_NOT_VIEW_OTHER_MEMBER_INQUIRY(6008, "다른 사용자가 작성한 문의글을 볼 수 없습니다."),
    CAN_NOT_DELETE_POST_WITH_MANAGER_ANSWER(6009, "매니저가 작성한 답변이 있으므로 해당 게시글을 삭제할 수 없습니다."),
    INVALID_SECRET(6010, "유효하지 않은 비밀글 설정입니다."),
    CAN_NOT_VIEW_INQUIRY_PASSWORD_NOT_MATCH(6010, "비밀번호가 다르므로 해당 문의글을 볼 수 없습니다."),

    CAN_NOT_UPDATE_REACTION(6011, "반응 정보를 수정할 수 없습니다."),

    NOT_FOUND_COMMENT(7001, "존재하지 않는 댓글입니다."),
    CAN_NOT_UPDATE_OTHER_MEMBER_COMMENT(7002,"다른 회원이 작성한 댓글을 수정할 수 없습니다."),
    CAN_NOT_DELETE_OTHER_MEMBER_COMMENT(7003, "다른 회원이 작성한 댓글을 삭제할 수 없습니다."),

    INVALID_ATTACHMENT_EXTENSION(8001, "허용하지 않은 첨부 파일의 확장자입니다."),
    INVALID_ATTACHMENT_SIZE(8002, "허용하지 않은 첨부 파일의 크기입니다."),
    INVALID_ATTACHMENT_ORIGINAL_NAME(8003, "허용하지 않은 첨부 파일명입니다."),
    NOT_FOUND_ATTACHMENT(8004, "존재하지 않는 첨부파일입니다."),
    CAN_NOT_UPDATE_THUMBNAIL(8005, "대표 이미지를 수정할 수 없습니다."),

    REQUEST_EXCEPTION(9001, "http 요청 에러입니다."),
    INVALID_PATH(9002, "잘못된 경로입니다."),
    UNPROCESSABLE_ENTITY(9003, "요청 데이터가 유효하지 않습니다."),
    UNHANDLED_EXCEPTION(9999, "예상치 못한 예외입니다.");

    private final int code;
    private final String message;

    ErrorType(final int code, final String message) {
        this.code = code;
        this.message = message;
    }
}
