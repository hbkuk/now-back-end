package com.now.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.now.exception.DuplicateUserException;
import com.now.exception.ErrorCode;
import com.now.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
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
        log.error(e.getMessages().stream().toString());

        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.DUPLICATE_USER);

        ObjectMapper objectMapper = new ObjectMapper();
        String fieldErrorsJson = objectMapper.writeValueAsString(e.getMessages());
        errorResponse.setDetail(fieldErrorsJson);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * JsonProcessingException을 처리하는 예외 핸들러
     * JSON 처리 오류를 처리
     *
     * @param e JsonProcessingException 인스턴스
     * @return ErrorResponse와 HttpStatus를 포함하는 ResponseEntity
     * @throws JsonProcessingException JSON 처리 오류가 발생할 경우 예외를 던집니다.
     */
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException e) {
        log.error("JSON 처리 오류: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.SERVER_INTERNAL_ERROR);
        errorResponse.setDetail(e.getMessage());

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
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_DATA);

        List<String> fieldErrors = e.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        ObjectMapper objectMapper = new ObjectMapper();
        String fieldErrorsJson = objectMapper.writeValueAsString(fieldErrors);
        errorResponse.setDetail(fieldErrorsJson);

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
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_PARAM);

        List<String> fieldErrors = e.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        ObjectMapper objectMapper = new ObjectMapper();
        String fieldErrorsJson = objectMapper.writeValueAsString(fieldErrors);
        errorResponse.setDetail(fieldErrorsJson);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}


