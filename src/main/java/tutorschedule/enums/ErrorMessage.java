package tutorschedule.enums;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    CURRENTLY_DATE_TAKEN("This date is reserved, choose different"),
    INCORRECT_ENTERED_DATE("Given date is incorrect"),
    EMAIL_NOT_EXIST("Not found the email you given"),
    NOT_ALLOWED_TO_REMOVE_SUBJECT("It's possible to delete a lesson at least 6h before the lesson"),
    INCORRECT_DATE_OF_LESSON("The given date of the lesson is incorrect"),
    INCORRECT_PASSWORD("The password you entered is incorrect"),
    INCORRECT_CONSTRUCTION_OF_EMAIL("Email address must contain @"),
    LESSON_NOT_FOUND("Not found lesson for Student with this email"),
    LOGIN_NEEDED("Invalid login data"),
    LOGIN_BEFORE_LOGOUT("You can't logOut before logIn"),
    NULL_GIVEN_PARAMETER("Given parameter can not be null"),
    USER_ALREADY_EXIST("User with given email already exist"),
    TEACHER_ALREADY_EXIST("Teacher with given email already exist"),
    STUDENT_NOT_FOUND("Not found Student with given Email address"),
    SUBJECT_NOT_EXIST("Not found subject with this name"),
    ILLEGAL_DATE_CHANGE("You can change date of subject only 12h before lesson"),
    TEACHER_NOT_FOUND("No teacher with the given email address was found"),
    USER_NOT_FOUND("Not found any user with given email address"),
    TERMS_NOT_FOUND("Not found lesson terms for teacher");

    private final String text;

    ErrorMessage(String text) {
        this.text = text;
    }
}
