package tutorschedule.exception;

import org.springframework.http.HttpStatus;
import tutorschedule.enums.ErrorMessage;

public class TermsNotFoundException extends TutorManagementException{
    public TermsNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.TERMS_NOT_FOUND);
    }
}
