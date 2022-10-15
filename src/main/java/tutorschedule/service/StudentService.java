package tutorschedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tutor_schedule.api.model.student.StudentDto;
import tutorschedule.entity.Student;
import tutorschedule.entity.User;
import tutorschedule.exception.InvalidEmailException;
import tutorschedule.exception.LogInException;
import tutorschedule.exception.UserAlreadyExistException;
import tutorschedule.exception.UserNotFoundException;
import tutorschedule.mapper.UserConverter;
import tutorschedule.repository.StudentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class StudentService {
    private final StudentRepository studentRepository;
    private final AuthenticationService authenticationService;

    public List<StudentDto> getStudents() {
        return studentRepository
                .findAll()
                .stream()
                .map(UserConverter::toStudentDto)
                .collect(Collectors.toList());
    }

    public boolean isLoggedIn(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Not found student with given id number");
                    throw new UserNotFoundException();
                });
        return authenticationService.isLoggedIn(student.getEmail());
    }

    public StudentDto addStudent(StudentDto studentDto) {
        Student student = UserConverter.studentDtoToStudent(studentDto);
        String studentEmail = student.getEmail();
        if (studentEmail == null || !studentEmail.contains("@")) {
            log.error("Email address is invalid");
            throw new InvalidEmailException();
        }
        if (studentRepository.existsByEmail(studentEmail)) {
            log.error("Not found student with given email address");
            throw new UserAlreadyExistException();
        }
        studentRepository.save(student);
        return UserConverter.toStudentDto(student);
    }

    public void deleteStudent(Long id) {
        if (!isLoggedIn(id)) {
            log.error("Student must be logged in system before update account");
            throw new LogInException();
        }
        studentRepository.deleteById(id);
    }

    public StudentDto updateStudent(Long id, StudentDto studentDto) {
        Student newStudent = UserConverter.studentDtoToStudent(studentDto);
        Student studentToChange = studentRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error("Not found student with given id number");
                    throw new UserNotFoundException();
                });
        if (!isLoggedIn(id)) {
            log.error("Student must be logged in system before update account");
            throw new LogInException();
        }
        changeStudentAccount(newStudent, studentToChange);
        return UserConverter.toStudentDto(studentToChange);
    }

    private void changeStudentAccount(User newStudent, User studentToUpdate) {
        Optional.ofNullable(newStudent.getEmail())
                .filter(e -> e.contains("@"))
                .ifPresent(studentToUpdate::setEmail);
        Optional.ofNullable(newStudent.getFirstName())
                .ifPresent(studentToUpdate::setFirstName);
        Optional.ofNullable(newStudent.getLastName())
                .ifPresent(studentToUpdate::setLastName);
        Optional.ofNullable(newStudent.getDateOfBirth())
                .ifPresent(studentToUpdate::setDateOfBirth);
        Optional.ofNullable(newStudent.getPassword())
                .ifPresent(studentToUpdate::setPassword);
        studentRepository.save((Student) studentToUpdate);
    }
}
