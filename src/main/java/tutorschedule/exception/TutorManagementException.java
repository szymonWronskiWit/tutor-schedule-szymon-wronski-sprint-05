package tutorschedule.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import tutorschedule.enums.ErrorMessage;

@Getter
public class TutorManagementException extends RuntimeException {
    private final HttpStatus httpStatus;

    public TutorManagementException(HttpStatus httpStatus, ErrorMessage errorMessage) {
        super(errorMessage.getText());
        this.httpStatus = httpStatus;
    }
}
