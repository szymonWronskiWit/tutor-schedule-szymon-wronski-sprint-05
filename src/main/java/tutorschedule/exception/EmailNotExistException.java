package tutorschedule.exception;

import org.springframework.http.HttpStatus;
import tutorschedule.enums.ErrorMessage;

public class EmailNotExistException extends TutorManagementException {

    public EmailNotExistException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.EMAIL_NOT_EXIST);
    }
}
