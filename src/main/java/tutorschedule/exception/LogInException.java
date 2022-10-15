package tutorschedule.exception;

import org.springframework.http.HttpStatus;
import tutorschedule.enums.ErrorMessage;

public class LogInException extends TutorManagementException {
    public LogInException() {
        super(HttpStatus.METHOD_NOT_ALLOWED, ErrorMessage.LOGIN_NEEDED);
    }
}
