package tutorschedule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tutor_schedule.api.model.student.StudentDto;
import tutorschedule.entity.Student;
import tutorschedule.exception.InvalidEmailException;
import tutorschedule.exception.LogInException;
import tutorschedule.exception.UserAlreadyExistException;
import tutorschedule.exception.UserNotFoundException;
import tutorschedule.repository.StudentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class StudentServiceTest {
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private StudentRepository studentRepository;
    private StudentService studentService;

    @BeforeEach
    void setup() {
        studentService = new StudentService(studentRepository, authenticationService);
    }

    @Test
    void shouldAddNewStudent() {
        //given
        StudentDto studentDto = UserUtils.buildStudentDto();
        when(studentRepository.existsByEmail(studentDto.getEmail())).thenReturn(false);

        //when
        StudentDto newStudentDto = studentService.addStudent(studentDto);

        //then
        assertNotNull(newStudentDto);
        assertEquals(newStudentDto.getEmail(), studentDto.getEmail());
    }

    @Test
    void shouldNotAddStudentWhenAlreadyExist() {
        //given
        Student student = UserUtils.buildStudent();
        StudentDto studentDto = UserUtils.buildStudentDto();
        when(studentRepository.existsByEmail(student.getEmail())).thenReturn(true);

        //when
        assertThrows(UserAlreadyExistException.class, () -> studentService.addStudent(studentDto));

        //then
        verify(studentRepository, times(0)).save(student);
    }

    @Test
    void shouldNotAddStudentWhenEmailIsInvalid() {
        //given
        Student student = UserUtils.buildStudent();
        StudentDto studentDto = UserUtils.buildStudentDto();
        studentDto.setEmail(null);

        //when
        assertThrows(InvalidEmailException.class, () -> studentService.addStudent(studentDto));

        //then
        verify(studentRepository, times(0)).existsByEmail(student.getEmail());
    }

    @Test
    void shouldDeleteStudent() {
        //given
        Student student = UserUtils.buildStudent();
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(authenticationService.isLoggedIn(student.getEmail())).thenReturn(true);

        //when
        studentService.deleteStudent(1L);

        //then
        verify(studentRepository).deleteById(1L);
    }

    @Test
    void shouldNotDeleteStudentWhenIsNotExist() {
        //given
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        assertThrows(UserNotFoundException.class, () -> studentService.deleteStudent(1L));

        //then
        verify(studentRepository, times(0)).deleteById(1L);
    }

    @Test
    void shouldNotDeleteStudentWhenIsNotLoggedIn() {
        //given
        Student student = UserUtils.buildStudent();
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(authenticationService.isLoggedIn(student.getEmail())).thenReturn(false);

        //when
        assertThrows(LogInException.class, () -> studentService.deleteStudent(1L));

        //then
        verify(studentRepository, times(0)).deleteById(1L);
    }

    @Test
    void shouldUpdateStudent() {
        //given
        Student student = UserUtils.buildStudent();
        StudentDto newStudent = UserUtils.buildStudentDto();
        newStudent.setEmail("marek.jurek@gmail.com");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(authenticationService.isLoggedIn(student.getEmail())).thenReturn(true);

        //when
        studentService.updateStudent(student.getId(), newStudent);

        //then
        verify(studentRepository).save(student);
    }

    @Test
    void shouldNotFoundStudentToUpdate() {
        //given
        StudentDto studentdto = UserUtils.buildStudentDto();
        Student student = UserUtils.buildStudent();
        when(studentRepository.findById(student.getId())).thenReturn(Optional.empty());

        //when
        assertThrows(UserNotFoundException.class, () -> studentService.updateStudent(1L, studentdto));

        //then
        verify(studentRepository, times(0)).save(student);
    }

    @Test
    void shouldNotUpdateStudentBecauseIsNotLoggedIn() {
        //given
        StudentDto studentDto = UserUtils.buildStudentDto();
        Student student = UserUtils.buildStudent();
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(authenticationService.isLoggedIn(student.getEmail())).thenReturn(false);

        //when
        assertThrows(LogInException.class, () -> studentService.updateStudent(1L, studentDto));

        //then
        verify(studentRepository, times(0)).save(student);
    }
}
