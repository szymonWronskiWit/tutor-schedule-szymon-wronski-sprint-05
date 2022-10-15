package tutorschedule.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tutorschedule.exception.TutorManagementException;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(TutorManagementException.class)
    public ResponseEntity<String> handleTutorMgmtExceptions(TutorManagementException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleUnCaughtExceptions(Throwable e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}