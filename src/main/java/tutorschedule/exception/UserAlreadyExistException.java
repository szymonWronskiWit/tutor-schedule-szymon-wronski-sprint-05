package tutorschedule.exception;

import org.springframework.http.HttpStatus;
import tutorschedule.enums.ErrorMessage;

public class UserAlreadyExistException extends TutorManagementException{
    public UserAlreadyExistException() {
        super(HttpStatus.CONFLICT, ErrorMessage.USER_ALREADY_EXIST);
    }
}
