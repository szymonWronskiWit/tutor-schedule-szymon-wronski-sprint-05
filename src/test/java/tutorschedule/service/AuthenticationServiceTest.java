package tutorschedule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tutorschedule.entity.Student;
import tutorschedule.entity.User;
import tutorschedule.exception.LogOutException;
import tutorschedule.exception.NullParameterException;
import tutorschedule.exception.UserNotFoundException;
import tutorschedule.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthenticationServiceTest {
    @MockBean
    private UserRepository userRepository;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(userRepository);
    }

    @Test
    void shouldLoginStudent() {
        //given
        Student student = UserUtils.buildStudent();
        when(userRepository.findByEmail(student.getEmail())).thenReturn(Optional.of(student));

        //when
        authenticationService.logIn(student.getEmail(), student.getPassword());

        //then
        assertTrue(authenticationService.loginStatuses.get(student.getEmail()));
    }

    @Test
    void shouldNotLoggedInBecauseNullLoginOrPassword() {
        //when
        assertThrows(NullParameterException.class, () -> authenticationService.logIn(null, "1234"));

        //then
        verify(userRepository, times(0)).findByEmail(null);
    }

    @Test
    void shouldNotLoggedInBecauseUserNotExist() {
        //given
        User user = UserUtils.buildStudent();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        //when
        assertThrows(UserNotFoundException.class, () -> authenticationService.logIn(user.getEmail(), user.getPassword()));

        //then
        assertNull(authenticationService.loginStatuses.get(user.getEmail()));
    }

    @Test
    void shouldLogOut() {
        //given
        Student student = UserUtils.buildStudent();
        authenticationService.loginStatuses.put(student.getEmail(), true);

        when(userRepository.findById(student.getId())).thenReturn(Optional.of(student));

        //when
        authenticationService.logOut(student.getId());

        //then
        assertFalse(authenticationService.loginStatuses.get(student.getEmail()));
    }

    @Test
    void shouldNotLogOutBecauseUserIsNotLoggedIn() {
        //given
        Student student = UserUtils.buildStudent();
        authenticationService.loginStatuses.put(student.getEmail(), false);

        when(userRepository.findById(student.getId())).thenReturn(Optional.of(student));

        //when
        assertThrows(LogOutException.class, () -> authenticationService.logOut(student.getId()));

        //then
        assertFalse(authenticationService.loginStatuses.get(student.getEmail()));
    }

    @Test
    void shouldNotLogOutBecauseUserIsNull() {
        //given
        Student student = UserUtils.buildStudent();
        authenticationService.loginStatuses.put(student.getEmail(), true);

        when(userRepository.findById(student.getId())).thenReturn(Optional.empty());

        //when
        assertThrows(UserNotFoundException.class, () -> authenticationService.logOut(1L));

        //then
        assertTrue(authenticationService.loginStatuses.get(student.getEmail()));
    }

    @Test
    void logOut() {
        //given
        Student student = UserUtils.buildStudent();

        authenticationService.loginStatuses.put(student.getEmail(), true);
        when(userRepository.findById(student.getId())).thenReturn(Optional.of(student));

        //when
        authenticationService.logOut(student.getId());
        boolean isLoggedIn = authenticationService.isLoggedIn(student.getEmail());

        //then
        assertFalse(isLoggedIn);
        assertThrows(LogOutException.class, () -> authenticationService.logOut(student.getId()));
    }
}
