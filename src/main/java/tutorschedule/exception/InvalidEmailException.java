package tutorschedule.exception;

import org.springframework.http.HttpStatus;
import tutorschedule.enums.ErrorMessage;

public class InvalidEmailException extends TutorManagementException {
    public InvalidEmailException() {
        super(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.INCORRECT_CONSTRUCTION_OF_EMAIL);
    }
}
