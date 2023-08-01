package com.now.common.exception;

import com.now.common.exception.dto.ErrorResponse;
import com.now.common.exception.dto.ValidationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

import static com.now.common.exception.ErrorType.UNHANDLED_EXCEPTION;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * BadRequestException이 발생했을 때 처리하는 메소드
     *
     * @param e 발생한 BadRequestException 객체
     * @return ErrorResponse 객체를 담은 ResponseEntity
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> badRequestExceptionHandler(final BadRequestException e) {
        log.warn("Bad Request Exception", e);
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getCode(), e.getMessage()));
    }

    /**
     * UnauthorizedException이 발생했을 때 처리하는 메소드
     *
     * @param e 발생한 UnauthorizedException 객체
     * @return ErrorResponse 객체를 담은 ResponseEntity
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> unauthorizedExceptionHandler(final UnauthorizedException e) {
        log.warn("Unauthorized Exception", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getCode(), e.getMessage()));
    }

    /**
     * ForbiddenException이 발생했을 때 처리하는 메소드
     *
     * @param e 발생한 ForbiddenException 객체
     * @return ErrorResponse 객체를 담은 ResponseEntity
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> forbiddenExceptionHandler(final ForbiddenException e) {
        log.warn("Forbidden Exception", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getCode(), e.getMessage()));
    }

    /**
     * MethodArgumentNotValidException 발생했을 때 처리하는 메소드
     *
     * @param e 발생한 MethodArgumentNotValidException 객체
     * @return ErrorResponse 객체를 담은 ResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e)  {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            errors.put(((FieldError) error).getField(), error.getDefaultMessage());
        });
        return ResponseEntity.unprocessableEntity().body(new ValidationErrorResponse(ErrorType.UNPROCESSABLE_ENTITY.getCode(), errors));
    }

    /**
     * BindException 발생했을 때 처리하는 메소드
     *
     * @param e 발생한 BindException 객체
     * @return ErrorResponse 객체를 담은 ResponseEntity
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(final BindException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("BindException: {}", message);
        return ResponseEntity.badRequest().body(new ErrorResponse(ErrorType.REQUEST_EXCEPTION.getCode(), message));
    }

    /**
     * NoHandlerFoundException 예외처리
     *
     * @param e 발생한 NoHandlerFoundException 예외 객체
     * @return 응답 결과
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("NoHandlerFoundException", e);
        return ResponseEntity.badRequest().body(new ErrorResponse(ErrorType.INVALID_PATH.getCode(), ErrorType.INVALID_PATH.getMessage()));
    }

    /**
     * 예외 처리되지 않은 다른 예외들을 처리하는 메소드
     *
     * @param e 예외 객체
     * @return ErrorResponse 객체를 담은 ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unhandledExceptionHandler(final Exception e) {
        log.error("예상치 못한 예외가 발생했습니다.", e);
        return ResponseEntity.internalServerError().body(new ErrorResponse(UNHANDLED_EXCEPTION.getCode(),
                UNHANDLED_EXCEPTION.getMessage()));
    }

}


