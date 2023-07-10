package com.now.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.now.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    // TODO: Blog 정리
    /**
     * 빈(Bean)으로 등록된 객체들은 싱글톤(Singleton) 스코프로 관리하나,
     * ObjectMapper는 스레드 안전(thread-safe)한 구현을 가지고 있어서 여러 쓰레드에서 동시에 사용해도 안전
     */
    private final ObjectMapper objectMapper;
    private final MessageSourceAccessor messageSource;

    @Autowired
    public GlobalExceptionHandler(ObjectMapper objectMapper, MessageSourceAccessor messageSource) {
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
    }

    /**
     * PermissionDeniedException 처리하는 예외 핸들러
     *
     * @param e PermissionDeniedException 인스턴스
     * @return ErrorResponse와 HttpStatus를 포함하는 ResponseEntity
     */
    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ErrorResponse> handlePermissionDeniedException(PermissionDeniedException e) {
        log.error(e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.PERMISSION_DENIED, messageSource);
        errorResponse.setDetail(e.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * ExpiredJwtException 처리하는 예외 핸들러
     *
     * @param e ExpiredJwtException 인스턴스
     * @return ErrorResponse와 HttpStatus를 포함하는 ResponseEntity
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException e) {
        log.error(e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.EXPIRED_TOKEN, messageSource);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * JWT 예외 처리 핸들러
     *
     * @param e 발생한 예외 객체
     * @return ErrorResponse와 HttpStatus를 포함하는 ResponseEntity
     */
    @ExceptionHandler({UnsupportedJwtException.class, MalformedJwtException.class, SignatureException.class})
    public ResponseEntity<ErrorResponse> handleJwtExceptions(Exception e) {
        log.error(e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_TOKEN, messageSource);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * AuthenticationFailedException 처리하는 예외 핸들러
     * 인증에 실패했을 때 발생하는 오류를 처리
     *
     * @param e AuthenticationFailedException 인스턴스
     * @return ErrorResponse와 HttpStatus를 포함하는 ResponseEntity
     */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationFailedException(AuthenticationFailedException e) {
        log.error(e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.AUTHENTICATION_FAILED, messageSource);
        errorResponse.setDetail(e.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * DuplicateUserException을 처리하는 예외 핸들러
     * 중복 사용자 오류를 처리
     *
     * @param e 중복 사용자 예외 인스턴스
     * @return ErrorResponse와 HttpStatus를 포함하는 ResponseEntity
     * @throws JsonProcessingException JSON 처리 오류가 발생할 경우 예외를 던집니다.
     */
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUserException(DuplicateUserException e) throws JsonProcessingException {
        log.error(e.getMessages().toString());

        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.DUPLICATE_USER, messageSource);
        errorResponse.setDetail(objectMapper.writeValueAsString(e.getMessages()));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * JsonProcessingException을 처리하는 예외 핸들러
     * JSON 처리 오류를 처리
     *
     * @param e JsonProcessingException 인스턴스
     * @return ErrorResponse와 HttpStatus를 포함하는 ResponseEntity
     */
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException e) {
        log.error("JSON 처리 오류: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.SERVER_INTERNAL_ERROR, messageSource);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * MethodArgumentNotValidException을 처리하는 예외 핸들러
     * 메서드 인자의 유효성 검증 오류를 처리
     *
     * @param e MethodArgumentNotValidException 인스턴스
     * @return ErrorResponse와 HttpStatus를 포함하는 ResponseEntity
     * @throws JsonProcessingException JSON 처리 오류가 발생할 경우 예외를 던집니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) throws JsonProcessingException {
        log.error(e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_DATA, messageSource);
        errorResponse.setDetail(objectMapper.writeValueAsString(extractFieldErrors(e)));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * BindException을 처리하는 예외 핸들러
     * 바인딩 오류를 처리
     *
     * @param e BindException 인스턴스
     * @return ErrorResponse와 HttpStatus를 포함하는 ResponseEntity
     * @throws JsonProcessingException JSON 처리 오류가 발생할 경우 예외를 던집니다.
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e) throws JsonProcessingException {
        log.error(e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_PARAM, messageSource);
        errorResponse.setDetail(objectMapper.writeValueAsString(extractFieldErrors(e)));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * BindException에서 필드 오류 메시지를 추출 후 리스트로 반환
     *
     * @param e BindException 인스턴스
     * @return 필드 오류 메시지 리스트
     */
    private List<String> extractFieldErrors(BindException e) {
        return e.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
    }
}


