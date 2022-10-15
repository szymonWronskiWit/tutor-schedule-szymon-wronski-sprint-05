package tutorschedule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tutor_schedule.api.model.teacher.SubjectDto;
import tutor_schedule.api.model.teacher.TeacherDto;
import tutorschedule.entity.LessonHours;
import tutorschedule.entity.Teacher;
import tutorschedule.exception.InvalidEmailException;
import tutorschedule.exception.LogInException;
import tutorschedule.exception.NullParameterException;
import tutorschedule.exception.SubjectNotExistException;
import tutorschedule.exception.UserAlreadyExistException;
import tutorschedule.exception.UserNotFoundException;
import tutorschedule.repository.LessonHoursRepository;
import tutorschedule.repository.TeacherRepository;
import tutorschedule.repository.TermRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TeacherServiceTest {
    @MockBean
    private TeacherRepository teacherRepository;
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private LessonHoursRepository lessonHoursRepository;
    @MockBean
    private TermRepository termRepository;
    private TeacherService teacherService;

    @BeforeEach
    void setup() {
        teacherService = new TeacherService(teacherRepository, authenticationService, lessonHoursRepository, termRepository);
    }

    private static final LocalDateTime LESSON_TIME = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0).withNano(0);

    @Test
    void shouldAddNewTeacher() {
        //given
        TeacherDto teacherDto = UserUtils.buildTeacherDto();
        when(teacherRepository.existsByEmail(teacherDto.getEmail())).thenReturn(false);

        //when
        TeacherDto newTeacherDto = teacherService.addTeacher(teacherDto);

        //then
        assertNotNull(newTeacherDto);
        assertEquals(newTeacherDto.getEmail(), teacherDto.getEmail());
    }

    @Test
    void shouldNotAddTeacherWhenAlreadyExist() {
        //given
        Teacher teacher = UserUtils.buildTeacher();
        TeacherDto teacherDto = UserUtils.buildTeacherDto();
        when(teacherRepository.existsByEmail(teacher.getEmail())).thenReturn(true);

        //when
        assertThrows(UserAlreadyExistException.class, () -> teacherService.addTeacher(teacherDto));

        //then
        verify(teacherRepository, times(0)).save(teacher);
    }

    @Test
    void shouldNotAddTeacherWhenEmailIsInvalid() {
        //given
        Teacher teacher = UserUtils.buildTeacher();
        TeacherDto teacherDto = UserUtils.buildTeacherDto();
        teacherDto.setEmail(null);

        //when
        assertThrows(InvalidEmailException.class, () -> teacherService.addTeacher(teacherDto));

        //then
        verify(teacherRepository, times(0)).existsByEmail(teacher.getEmail());
    }

    @Test
    void shouldGetAllTeachers() {
        Teacher teacher = UserUtils.buildTeacher();

        when(teacherRepository.findAll()).thenReturn(List.of(teacher));

        //when
        List<TeacherDto> teachers = teacherService.getAllTeachers();

        //then
        assertEquals(1, teachers.size());
    }

    @Test
    void shouldDeleteTeacher() {
        //given
        Teacher teacher = UserUtils.buildTeacher();
        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(authenticationService.isLoggedIn(teacher.getEmail())).thenReturn(true);

        //when
        teacherService.deleteTeacher(teacher.getId());

        //then
        verify(teacherRepository).deleteById(teacher.getId());
    }

    @Test
    void shouldNotDeleteTeacherWhenIsNotExist() {
        //given
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        //when
        assertThrows(UserNotFoundException.class, () -> teacherService.deleteTeacher(1L));

        //then
        verify(teacherRepository, times(0)).deleteById(1L);
    }

    @Test
    void shouldNotDeleteTeacherWhenIsNotLoggedIn() {
        //given
        Teacher teacher = UserUtils.buildTeacher();
        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(authenticationService.isLoggedIn(teacher.getEmail())).thenReturn(false);

        //when
        assertThrows(LogInException.class, () -> teacherService.deleteTeacher(teacher.getId()));

        //then
        verify(teacherRepository, times(0)).deleteById(teacher.getId());
    }

    @Test
    void shouldUpdateTeacher() {
        //given
        Teacher teacher = UserUtils.buildTeacher();
        TeacherDto newTeacher = UserUtils.buildTeacherDto();
        newTeacher.setEmail("marek.jurek@gmail.com");
        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(authenticationService.isLoggedIn(teacher.getEmail())).thenReturn(true);

        //when
        teacherService.updateTeacher(teacher.getId(), newTeacher);

        //then
        verify(teacherRepository).save(teacher);
    }

    @Test
    void shouldNotFoundTeacherToUpdate() {
        //given
        TeacherDto teacherDto = UserUtils.buildTeacherDto();
        Teacher teacher = UserUtils.buildTeacher();
        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.empty());

        //when
        assertThrows(UserNotFoundException.class, () -> teacherService.updateTeacher(teacher.getId(), teacherDto));

        //then
        verify(teacherRepository, times(0)).save(teacher);
    }

    @Test
    void shouldNotUpdateStudentBecauseIsNotLoggedIn() {
        //given
        TeacherDto teacherDto = UserUtils.buildTeacherDto();
        Teacher teacher = UserUtils.buildTeacher();
        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(authenticationService.isLoggedIn(teacher.getEmail())).thenReturn(false);

        //when
        assertThrows(LogInException.class, () -> teacherService.updateTeacher(teacher.getId(), teacherDto));

        //then
        verify(teacherRepository, times(0)).save(teacher);
    }

    @Test
    void shouldAssignSubjectToTeacher() {
        //given
        Teacher teacher = UserUtils.buildTeacher();
        SubjectDto subject = SubjectDto.BIOLOGY;
        List<SubjectDto> teacherSubjectsBeforeAddNew = Arrays
                .asList(teacher.getSubjects()
                        .toArray(new SubjectDto[0]));

        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));

        //when
        teacherService.addSubject(teacher.getId(), subject);

        //then
        assertEquals(2, teacher.getSubjects().size());
        assertNotEquals(teacherSubjectsBeforeAddNew.size(), teacher.getSubjects().size());
    }

    @Test
    void shouldThrowUserNotFoundExceptionDuringAssignLesson() {
        //given
        Teacher teacher = UserUtils.buildTeacher();
        SubjectDto subject = SubjectDto.MATHEMATICS;

        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.empty());

        //when
        assertThrows(UserNotFoundException.class, () -> teacherService.addSubject(teacher.getId(), subject));

        //then
        verify(teacherRepository, times(0)).save(teacher);
    }

    @Test
    void shouldThrowNullParameterExceptionWhenSubjectIsNull() {
        //given
        Teacher teacher = UserUtils.buildTeacher();

        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));

        //when
        assertThrows(NullParameterException.class, () -> teacherService.addSubject(teacher.getId(), null));

        //then
        verify(teacherRepository, times(0)).save(teacher);
    }

    @Test
    void shouldRemoveSubject() {
        //given
        Teacher teacher = UserUtils.buildTeacher();
        SubjectDto teacherSubject = SubjectDto.MATHEMATICS;

        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));

        //when
        teacherService.removeSubject(teacher.getId(), teacherSubject);

        //then
        verify(teacherRepository).save(teacher);
        assertEquals(0, teacher.getSubjects().size());
    }

    @Test
    void shouldThrowUserNotFoundExceptionDuringRemoveSubject() {
        //given
        Teacher teacher = UserUtils.buildTeacher();
        SubjectDto subject = SubjectDto.MATHEMATICS;

        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.empty());

        //when
        assertThrows(UserNotFoundException.class, () -> teacherService.removeSubject(teacher.getId(), subject));

        //then
        verify(teacherRepository, times(0)).save(teacher);
    }

    @Test
    void shouldThrowNullParameterExceptionBecauseSubjectIsNull() {
        //given
        Teacher teacher = UserUtils.buildTeacher();

        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));

        //when
        assertThrows(NullParameterException.class, () -> teacherService.removeSubject(teacher.getId(), null));

        //then
        verify(teacherRepository, times(0)).save(teacher);
    }

    @Test
    void shouldThrowSubjectNotExistExceptionBecauseTeacherNotContainThisSubject() {
        //given
        Teacher teacher = UserUtils.buildTeacher();
        SubjectDto subjectThatTeacherNotHave = SubjectDto.BIOLOGY;

        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));

        //when
        assertThrows(SubjectNotExistException.class,
                () -> teacherService.removeSubject(teacher.getId(), subjectThatTeacherNotHave));

        //then
        verify(teacherRepository, times(0)).save(teacher);
    }

    @Test
    void shouldGetAllLessonsTime() {
        //given
        Teacher teacher = UserUtils.buildTeacher();
        SubjectDto subject = SubjectDto.MATHEMATICS;

        LessonHours lessonHours = LessonHours.builder()
                .date(LESSON_TIME)
                .subject(subject)
                .build();

        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(lessonHoursRepository.findAllBySubject(subject)).thenReturn(List.of(lessonHours));

        //when
        List<LocalDateTime> allLessonDates = teacherService.getAllLessonDates(teacher.getId(), subject);

        //then
        verify(lessonHoursRepository).findAllBySubject(subject);
        assertEquals(1, allLessonDates.size());
    }

    @Test
    void shouldThrowUserNotFoundExceptionDuringSearchingTeacher() {
        //given
        Teacher teacher = UserUtils.buildTeacher();
        SubjectDto subject = SubjectDto.MATHEMATICS;

        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.empty());

        //when
        assertThrows(UserNotFoundException.class, () -> teacherService.getAllLessonDates(teacher.getId(), subject));

        //then
        verify(lessonHoursRepository, times(0)).findAllBySubject(subject);
    }

    @Test
    void shouldThrowSubjectNotExistExceptionDuringCheckingIfTeacherContainGivenSubject() {
        //given
        Teacher teacher = UserUtils.buildTeacher();
        SubjectDto subject = SubjectDto.BIOLOGY;

        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));

        //when
        assertThrows(SubjectNotExistException.class, () -> teacherService.getAllLessonDates(teacher.getId(), subject));

        //then
        verify(lessonHoursRepository, times(0)).findAllBySubject(subject);
    }
}
