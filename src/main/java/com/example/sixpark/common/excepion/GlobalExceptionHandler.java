package com.example.sixpark.common.excepion;

import com.example.sixpark.common.response.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    // Validation 실패 (400 Bad request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        // 첫 번째 에러 메시지를 사용
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("입력값이 유효하지 않습니다.");

        log.error("Validation 실패: {}", message);

        // ApiResponse를 사용하여 일관된 형식으로 반환 (data는 null)
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(message));
    }

    // 커스텀 예외 처리
    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        log.error("CustomException 발생 - ErrorCode: {}, Message: {}",
                e.getErrorMessage(), e.getMessage());

        // ApiResponse를 사용하여 일관된 형식으로 반환
        return ResponseEntity
                .status(e.getErrorMessage().getStatus())
                .body(ApiResponse.error(e.getMessage()));
    }

    // 예상하지 못한 예외 처리 (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("예상하지 못한 예외 발생: ", e);

        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error("서버 내부 오류가 발생했습니다."));
    }
}
