package tutorschedule.exception;

import org.springframework.http.HttpStatus;
import tutorschedule.enums.ErrorMessage;

public class IllegalRemoveSubjectException extends TutorManagementException {
    public IllegalRemoveSubjectException() {
        super(HttpStatus.METHOD_NOT_ALLOWED, ErrorMessage.NOT_ALLOWED_TO_REMOVE_SUBJECT);
    }
}
