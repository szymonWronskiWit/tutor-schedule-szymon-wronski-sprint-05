package tutorschedule.exception;

import org.springframework.http.HttpStatus;
import tutorschedule.enums.ErrorMessage;

public class IncorrectPasswordException extends TutorManagementException {
    public IncorrectPasswordException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.INCORRECT_PASSWORD);
    }
}
