package tutorschedule.exception;

import org.springframework.http.HttpStatus;
import tutorschedule.enums.ErrorMessage;

public class SubjectNotExistException extends TutorManagementException {
    public SubjectNotExistException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.SUBJECT_NOT_EXIST);
    }
}
