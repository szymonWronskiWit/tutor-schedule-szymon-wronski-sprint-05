package tutorschedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tutor_schedule.api.model.teacher.SubjectDto;
import tutor_schedule.api.model.teacher.TeacherDto;
import tutorschedule.entity.LessonHours;
import tutorschedule.entity.Teacher;
import tutorschedule.entity.User;
import tutorschedule.exception.InvalidEmailException;
import tutorschedule.exception.LogInException;
import tutorschedule.exception.NullParameterException;
import tutorschedule.exception.SubjectNotExistException;
import tutorschedule.exception.UserAlreadyExistException;
import tutorschedule.exception.UserNotFoundException;
import tutorschedule.mapper.UserConverter;
import tutorschedule.repository.LessonHoursRepository;
import tutorschedule.repository.TeacherRepository;
import tutorschedule.repository.TermRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final AuthenticationService authenticationService;
    private final LessonHoursRepository lessonHoursRepository;
    private final TermRepository termRepository;

    public TeacherDto addTeacher(TeacherDto teacherDto) {
        Teacher teacher = UserConverter.teacherDtoToTeacher(teacherDto);
        String teacherEmail = teacher.getEmail();
        if (teacherRepository.existsByEmail(teacherEmail)) {
            log.error("Teacher With given email address already exist");
            throw new UserAlreadyExistException();
        }
        if (teacherEmail == null || !teacherEmail.contains("@")) {
            log.error("The email is incorrect");
            throw new InvalidEmailException();
        }
        teacherRepository.save(teacher);
        return UserConverter.toTeacherDto(teacher);
    }

    public List<TeacherDto> getAllTeachers() {
        return teacherRepository
                .findAll()
                .stream()
                .map(UserConverter::toTeacherDto)
                .collect(Collectors.toList());
    }

    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> {
            log.error("Not found teacher with given id");
            throw new UserNotFoundException();
        });
        if (!authenticationService.isLoggedIn(teacher.getEmail())) {
            log.error("Teacher must be logged in system before delete account");
            throw new LogInException();
        }
        teacherRepository.deleteById(id);
    }

    public TeacherDto updateTeacher(Long id, TeacherDto teacherDto) {
        Teacher newTeacher = UserConverter.teacherDtoToTeacher(teacherDto);
        Teacher teacherToUpdate = teacherRepository.findById(id).orElseThrow(() -> {
            log.error("Not found teacher with given id");
            throw new UserNotFoundException();
        });
        if (!authenticationService.isLoggedIn(teacherToUpdate.getEmail())) {
            log.error("Student must be logged in system before update account");
            throw new LogInException();
        }
        changeTeacherAccount(newTeacher, teacherToUpdate);
        return UserConverter.toTeacherDto(teacherToUpdate);
    }

    public void changeTeacherAccount(User newTeacher, User teacherToUpdate) {
        Optional.ofNullable(newTeacher.getEmail())
                .filter(e -> e.contains("@"))
                .ifPresent(teacherToUpdate::setEmail);
        Optional.ofNullable(newTeacher.getFirstName())
                .ifPresent(teacherToUpdate::setFirstName);
        Optional.ofNullable(newTeacher.getLastName())
                .ifPresent(teacherToUpdate::setLastName);
        Optional.ofNullable(newTeacher.getDateOfBirth())
                .ifPresent(teacherToUpdate::setDateOfBirth);
        Optional.ofNullable(newTeacher.getPassword())
                .ifPresent(teacherToUpdate::setPassword);
        teacherRepository.save((Teacher) teacherToUpdate);
    }

    public Set<SubjectDto> addSubject(Long id, SubjectDto subject) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> {
            log.error("Not found teacher with given id");
            throw new UserNotFoundException();
        });
        if (subject == null) {
            log.info("subject is null");
            throw new NullParameterException();
        }
        teacher.getSubjects().add(subject);
        teacherRepository.save(teacher);
        return teacher.getSubjects();
    }

    public void removeSubject(Long id, SubjectDto subject) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> {
            log.error("Not found teacher with given id");
            throw new UserNotFoundException();
        });
        if (subject == null) {
            log.error("subject is null");
            throw new NullParameterException();
        }
        boolean hasTeacherContainGivenSubject = teacher.getSubjects().contains(subject);
        if (!hasTeacherContainGivenSubject) {
            log.error("Teacher has not contain given subject");
            throw new SubjectNotExistException();
        }
        teacher.getSubjects().remove(subject);
        teacherRepository.save(teacher);
    }

    public List<LocalDateTime> getAllLessonDates(long id, SubjectDto subject) {
        Teacher teacher = teacherRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error("Not found teacher with given id");
                    throw new UserNotFoundException();
                });
        boolean hasTeacherSubject = teacher.getSubjects().contains(subject);
        if (!hasTeacherSubject) {
            log.error("Teacher not contains given subject");
            throw new SubjectNotExistException();
        }
        return lessonHoursRepository.findAllBySubject(subject)
                .stream()
                .map(LessonHours::getDate)
                .toList();
    }
}
