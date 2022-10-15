package tutorschedule.exception;

import org.springframework.http.HttpStatus;
import tutorschedule.enums.ErrorMessage;

public class LessonNotFoundException extends TutorManagementException {
    public LessonNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.LESSON_NOT_FOUND);
    }
}
