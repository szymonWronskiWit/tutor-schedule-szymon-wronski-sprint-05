package tutorschedule.exception;

import org.springframework.http.HttpStatus;
import tutorschedule.enums.ErrorMessage;

public class LogOutException extends TutorManagementException {
    public LogOutException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.LOGIN_BEFORE_LOGOUT);
    }
}
