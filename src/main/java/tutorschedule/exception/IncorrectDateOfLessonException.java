package tutorschedule.exception;

import org.springframework.http.HttpStatus;
import tutorschedule.enums.ErrorMessage;

public class IncorrectDateOfLessonException extends TutorManagementException {
    public IncorrectDateOfLessonException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.INCORRECT_DATE_OF_LESSON);
    }
}
