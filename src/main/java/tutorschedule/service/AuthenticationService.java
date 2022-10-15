package tutorschedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tutorschedule.entity.User;
import tutorschedule.exception.IncorrectPasswordException;
import tutorschedule.exception.LogOutException;
import tutorschedule.exception.NullParameterException;
import tutorschedule.exception.UserNotFoundException;
import tutorschedule.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    Map<String, Boolean> loginStatuses = new HashMap<>();

    public boolean logIn(String email, String password) {
        if (email == null || password == null) {
            log.info("Email or password given from user is null");
            throw new NullParameterException();
        }
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        validatePassword(user.getPassword(), password);
        loginStatuses.put(email, true);
        return true;
    }

    private void validatePassword(String userPassword, String requestPassword) {
        if (!requestPassword.equals(userPassword)) {
            log.info("Given password is not equal to the one from database");
            throw new IncorrectPasswordException();
        }
    }

    public void logOut(Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(UserNotFoundException::new);
        if (!isLoggedIn(user.getEmail())) {
            log.info("The user tries to log out without being logged in");
            throw new LogOutException();
        }
        loginStatuses.replace(user.getEmail(), false);
    }

    public boolean isLoggedIn(String email) {
        if (email == null) {
            log.info("The email given by the user is null");
            return false;
        }
        if (!loginStatuses.containsKey(email)) {
            log.info("Any teacher and any student contain given email address");
            return false;
        }
        return loginStatuses.get(email);
    }
}
