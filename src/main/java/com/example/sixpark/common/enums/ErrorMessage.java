package com.example.sixpark.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

    // 400 Bad Request : 클라이언트 수정
    MUST_USERNAME_PASSWORD(HttpStatus.BAD_REQUEST, "username과 password는 필수입니다"),
    MUST_TITLE_TASK_USER(HttpStatus.BAD_REQUEST, "제목과 담당자는 필수입니다."),
    MUST_TEAM_NAME(HttpStatus.BAD_REQUEST, "팀 이름을 필수입니다."),
    MUST_COMMENT_CONTENT(HttpStatus.BAD_REQUEST, "댓글 내용은 필수입니다."),
    MUST_SEARCH_TERM(HttpStatus.BAD_REQUEST, "검색어를 입력해주세요."),
    EXIST_AND_NEW_PASSWORD(HttpStatus.BAD_REQUEST, "기존 비밀번호와 다른 비밀번호를 입력해주세요."),
    NOT_CORRECT_EMAIL_FORM(HttpStatus.BAD_REQUEST, "올바른 이메일 형식이 아닙니다."),
    NOT_CORRECT_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 요청 파라미터입니다."),
    NOT_CORRECT_TASK_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 상태값입니다."),
    SEAT_ALREADY_SELECTED(HttpStatus.BAD_REQUEST, "이미 선택된 좌석입니다."),
    // 401 Unauthorized : 로그인 필요(인증)
    NEED_TO_VALID_TOKEN(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    NOT_MATCH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
    NOT_MATCH_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    // 403 Forbidden : 권한 거부(인가)
    ONLY_OWNER_ACCESS(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    NOT_MODIFY_AUTHORIZED(HttpStatus.FORBIDDEN, "수정 권한이 없습니다."),
    NOT_MODIFY_COMMENT_AUTHORIZED(HttpStatus.FORBIDDEN, "댓글을 수정할 권한이 없습니다."),
    NOT_TASK_DELETE_AUTHORIZED(HttpStatus.FORBIDDEN, "작업을 삭제할 권한이 없습니다."),
    NOT_COMMENT_DELETE_AUTHORIZED(HttpStatus.FORBIDDEN, "댓글을 삭제할 권한이 없습니다."),
    NOT_DELETE_AUTHORIZED(HttpStatus.FORBIDDEN, "삭제 권한이 없습니다"),
    NOT_REMOVE_AUTHORIZED(HttpStatus.FORBIDDEN, "제거 권한이 없습니다"),
    REJECT_AUTHORIZED(HttpStatus.FORBIDDEN, "해당 기능을 사용할 권한이 없습니다. 관리자에게 문의해주세요"), // 관리자 권한이 필요한 페이지 접근할때
    // 404 Not Found : 리소스가 없음
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    NOT_FOUND_TASK_USER(HttpStatus.NOT_FOUND, "담당자를 찾을 수 없습니다."),
    NOT_FOUND_TASK(HttpStatus.NOT_FOUND, "작업을 찾을 수 없습니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    NOT_FOUND_TEAM(HttpStatus.NOT_FOUND, "팀을 찾을 수 없습니다."),
    NOT_FOUND_TEAM_MEMBER(HttpStatus.NOT_FOUND, "팀 멤버를 찾을 수 없습니다."),
    NOT_FOUND_SEAT(HttpStatus.NOT_FOUND, "좌석을 찾을 수 없습니다."),
    // 409 Conflict : 중복 데이터
    EXIST_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    EXIST_USERNAME(HttpStatus.CONFLICT, "이미 사용 중인 아이디 이름입니다."),
    EXIST_NAME(HttpStatus.CONFLICT, "이미 존재하는 사용자명입니다."),
    EXIST_TEAM_NAME(HttpStatus.CONFLICT, "이미 존재하는 팀 이름입니다."),
    EXIST_TEAM_MEMBER_NOT_DELETE(HttpStatus.CONFLICT, "팀에 멤버가 존재하여 삭제할 수 없습니다."),
    EXIST_TEAM_MEMBER(HttpStatus.CONFLICT, "이미 팀에 속한 멤버입니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
