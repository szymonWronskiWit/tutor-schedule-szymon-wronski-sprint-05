package tutorschedule.exception;

import org.springframework.http.HttpStatus;
import tutorschedule.enums.ErrorMessage;

public class NullParameterException extends TutorManagementException {
    public NullParameterException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.NULL_GIVEN_PARAMETER);
    }
}
