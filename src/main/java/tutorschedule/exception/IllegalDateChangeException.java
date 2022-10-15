package tutorschedule.exception;

import org.springframework.http.HttpStatus;
import tutorschedule.enums.ErrorMessage;

public class IllegalDateChangeException extends TutorManagementException {
    public IllegalDateChangeException() {
        super(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.ILLEGAL_DATE_CHANGE);
    }
}
