package app.web.exception.handler;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import app.dto.exception.ExceptionInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by GW95IB on 2017-05-25.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionInfo> defaultExceptionHandler(
            HttpServletRequest request,
            Exception exception) {

        HttpStatus statusCode;

        if (AnnotationUtils.isAnnotationDeclaredLocally(ResponseStatus.class, exception.getClass())) {
            ResponseStatus responseStatus = AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class);
            statusCode = responseStatus.code();
        } else {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ExceptionInfo exceptionInfo = new ExceptionInfo(request.getRequestURI(), exception.getClass().getSimpleName(), exception.getMessage());

        return new ResponseEntity<>(exceptionInfo, statusCode);
    }
}
