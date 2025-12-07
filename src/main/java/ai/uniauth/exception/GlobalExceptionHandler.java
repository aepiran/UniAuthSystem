package ai.uniauth.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

//@RestControllerAdvice
public class GlobalExceptionHandler {

//    // Custom Access Denied 403
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex,
//                                                                   HttpServletRequest request,
//                                                                   Authentication auth) {
//
//        String username = auth != null && auth.getName() != null ? auth.getName() : "anonymous";
//
//        ApiResponse response = ApiResponse.builder()
//                .success(false)
//                .code("ACCESS_DENIED")
//                .message("Bạn không có quyền truy cập tài nguyên này")
//                .detail("User '" + username + "' attempted to access: " + request.getRequestURI())
//                .timestamp(Instant.now())
//                .build();
//
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
//    }
//
//    // Optional: custom 401 khi chưa login
//    @ExceptionHandler(AuthenticationException.class)
//    public ResponseEntity<ApiResponse> handleAuthenticationException(AuthenticationException ex) {
//        ApiResponse response = ApiResponse.builder()
//                .success(false)
//                .code("UNAUTHENTICATED")
//                .message("Vui lòng đăng nhập")
//                .timestamp(Instant.now())
//                .build();
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//    }
}