package tutorschedule.exception;

import org.springframework.http.HttpStatus;
import tutorschedule.enums.ErrorMessage;

public class CurrentlyDateTakenException extends TutorManagementException {
    public CurrentlyDateTakenException() {
        super(HttpStatus.CONFLICT, ErrorMessage.CURRENTLY_DATE_TAKEN);
    }
}
