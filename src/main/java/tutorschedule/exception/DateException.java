package tutorschedule.exception;

import org.springframework.http.HttpStatus;
import tutorschedule.enums.ErrorMessage;

public class DateException extends TutorManagementException {
    public DateException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.INCORRECT_ENTERED_DATE);
    }
}
