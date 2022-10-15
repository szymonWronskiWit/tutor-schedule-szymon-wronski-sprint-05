package tutorschedule.exception;

import org.springframework.http.HttpStatus;
import tutorschedule.enums.ErrorMessage;

public class UserNotFoundException extends TutorManagementException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND);
    }
}
