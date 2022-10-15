package tutorschedule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tutor_schedule.api.model.teacher.SubjectDto;
import tutorschedule.dto.LessonDto;
import tutorschedule.dto.StudentLessonDto;
import tutorschedule.entity.Lesson;
import tutorschedule.entity.LessonHours;
import tutorschedule.entity.Student;
import tutorschedule.entity.Teacher;
import tutorschedule.entity.Term;
import tutorschedule.exception.CurrentlyDateTakenException;
import tutorschedule.exception.DateException;
import tutorschedule.exception.IllegalDateChangeException;
import tutorschedule.exception.IllegalRemoveSubjectException;
import tutorschedule.exception.LessonNotFoundException;
import tutorschedule.exception.LogInException;
import tutorschedule.exception.NullParameterException;
import tutorschedule.repository.LessonHoursRepository;
import tutorschedule.repository.LessonRepository;
import tutorschedule.repository.StudentRepository;
import tutorschedule.repository.TeacherRepository;
import tutorschedule.repository.TermRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class LessonServiceTest {
    @MockBean
    private StudentService studentService;
    @MockBean
    private LessonRepository lessonRepository;
    @MockBean
    private TeacherRepository teacherRepository;
    @MockBean
    private TermRepository termRepository;
    @MockBean
    private LessonHoursRepository lessonHoursRepository;
    @MockBean
    private StudentRepository studentRepository;
    private LessonService lessonService;

    @BeforeEach
    void setup() {
        lessonService = new LessonService(studentService, teacherRepository, lessonRepository, studentRepository, termRepository, lessonHoursRepository);
    }

    private static final LocalDateTime LESSON_TIME = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0).withNano(0);
    private static final SubjectDto DEFAULT_SUBJECT = SubjectDto.MATHEMATICS;

    @Test
    void shouldAssignLessonToStudent() {
        //given
        Student student = UserUtils.buildStudent();
        Teacher teacher = UserUtils.buildTeacher();
        Lesson newLesson = UserUtils.buildLesson();
        Lesson lesson = UserUtils.buildLesson();
        lesson.setDate(LESSON_TIME.plusDays(1));
        LessonDto lessonDto = UserUtils.buildLessonDto();

        Term term = Term.builder()
                .lessonTime(LESSON_TIME)
                .isFree(false)
                .teacher(teacher)
                .subject(DEFAULT_SUBJECT)
                .build();

        LessonHours lessonHour = LessonHours.builder()
                .date(LESSON_TIME)
                .subject(DEFAULT_SUBJECT)
                .build();

        List<Lesson> lessonList = new ArrayList<>();
        lessonList.add(lesson);

        List<LessonHours> lessonHoursList = new ArrayList<>();
        lessonHoursList.add(lessonHour);

        when(studentRepository.findByEmail(student.getEmail())).thenReturn(Optional.of(student));
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.existsByEmail(teacher.getEmail())).thenReturn(true);
        when(studentService.isLoggedIn(student.getId())).thenReturn(true);
        when(teacherRepository.findTeacherBySubjects(DEFAULT_SUBJECT)).thenReturn(Optional.of(teacher));
        when(lessonHoursRepository.findAllBySubject(DEFAULT_SUBJECT)).thenReturn(lessonHoursList);
        when(teacherRepository.findByEmail(teacher.getEmail())).thenReturn(teacher);
        when(lessonHoursRepository.findAllBySubject(DEFAULT_SUBJECT)).thenReturn(lessonHoursList);
        when(lessonRepository.findLessonsByStudentId(1L)).thenReturn(lessonList);
        when(lessonHoursRepository.findLessonHoursByDateAndSubject(LESSON_TIME, DEFAULT_SUBJECT)).thenReturn(lessonHour);

        when(lessonRepository.save(newLesson)).thenReturn(newLesson).getMock();
        when(termRepository.save(term)).thenReturn(term);

        //when
        List<StudentLessonDto> lessons = lessonService.assignLesson(lessonDto);

        //then
        assertNotNull(lessonDto);
        assertEquals(lessonList.size(), lessons.size());

        for (StudentLessonDto getLessonDto : lessons) {
            assertEquals(newLesson.getTeacherEmail(), getLessonDto.getTeacherEmail());
            assertEquals(newLesson.getTeacherFirstName(), getLessonDto.getTeacherFirstName());
            assertEquals(newLesson.getSubject(), lessonDto.getSubject());
        }
    }

    @Test
    void shouldThrowLogInExceptionDuringAssignLesson() {
        //given
        Student student = UserUtils.buildStudent();
        LessonDto lessonDto = UserUtils.buildLessonDto();
        Lesson lesson = UserUtils.buildLesson();

        when(studentRepository.findByEmail(student.getEmail())).thenReturn(Optional.of(student));
        when(lessonRepository.findLessonsByStudentId(student.getId())).thenReturn(List.of(lesson));
        when(studentService.isLoggedIn(student.getId())).thenReturn(false);

        //when
        assertThrows(LogInException.class, () -> lessonService.assignLesson(lessonDto));

        //then
        verify(lessonRepository, times(0)).save(new Lesson());
    }

    @Test
    void shouldThrowCurrentlyDateTakenExceptionDuringAssignLesson() {
        //given
        Student student = UserUtils.buildStudent();
        Teacher teacher = UserUtils.buildTeacher();
        Lesson lesson = UserUtils.buildLesson();
        LessonDto lessonDto = UserUtils.buildLessonDto();

        List<Lesson> lessonList = new ArrayList<>();
        lessonList.add(lesson);

        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.findTeacherBySubjects(DEFAULT_SUBJECT)).thenReturn(Optional.of(teacher));
        when(studentRepository.findByEmail(student.getEmail())).thenReturn(Optional.of(student));
        when(lessonRepository.findLessonsByStudentId(student.getId())).thenReturn(lessonList);
        when(studentService.isLoggedIn(student.getId())).thenReturn(true);

        //when
        assertThrows(CurrentlyDateTakenException.class, () -> lessonService.assignLesson(lessonDto));

        //then
        verify(lessonRepository, times(0)).save(new Lesson());
    }

    @Test
    void shouldCancelTheLesson() {
        //given
        Student student = UserUtils.buildStudent();
        Teacher teacher = UserUtils.buildTeacher();
        Lesson lesson = UserUtils.buildLesson();
        lesson.setDate(LESSON_TIME.plusDays(1));

        when(studentService.isLoggedIn(student.getId())).thenReturn(true);
        when(lessonRepository.findLessonsByDate(LESSON_TIME.plusDays(1))).thenReturn(lesson);
        when(teacherRepository.findTeacherBySubjects(DEFAULT_SUBJECT)).thenReturn(Optional.of(teacher));
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(lessonRepository.findById(lesson.getId())).thenReturn(Optional.of(lesson));

        //when
        List<LocalDateTime> listOfStudentDates = lessonService.cancelLesson(student.getId(), lesson.getId());

        //then
        assertNotEquals(1, listOfStudentDates.size());

    }

    @Test
    void shouldThrowLogInExceptionDuringCancelLesson() {
        //given
        Student student = UserUtils.buildStudent();
        Lesson lesson = Lesson.builder()
                .id(1L)
                .date(LocalDateTime.now())
                .build();

        when(studentService.isLoggedIn(student.getId())).thenReturn(false);
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(lessonRepository.findById(lesson.getId())).thenReturn(Optional.of(lesson));
        //when
        assertThrows(LogInException.class, () -> lessonService.cancelLesson(student.getId(), lesson.getId()));

        //then
        verify(lessonRepository, times(0)).findLessonsByDate(LocalDateTime.now());
    }

    @Test
    void shouldThrowLessonNotFoundExceptionDuringCancelLesson() {
        //given
        Student student = UserUtils.buildStudent();
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));

        //when
        assertThrows(LessonNotFoundException.class, () -> lessonService.cancelLesson(student.getId(), 1L));

        //then
        verify(termRepository, times(0)).save(new Term());
    }

    @Test
    void shouldThrowDateExceptionDuringCancelLesson() {
        //given
        Student student = UserUtils.buildStudent();
        Lesson lesson = Lesson.builder()
                .id(1L)
                .date(null)
                .build();

        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(lessonRepository.findById(lesson.getId())).thenReturn(Optional.of(lesson));
        when(studentService.isLoggedIn(student.getId())).thenReturn(true);

        //when
        assertThrows(DateException.class, () -> lessonService.cancelLesson(student.getId(), lesson.getId()));

        //then
        verify(lessonRepository, times(0)).delete(new Lesson());
    }

    @Test
    void shouldThrowIllegalRemoveSubjectExceptionDuringCancelLesson() {
        //given
        Student student = UserUtils.buildStudent();
        Teacher teacher = UserUtils.buildTeacher();

        Lesson lesson = UserUtils.buildLesson();
        lesson.setDate(LocalDateTime.now().plusHours(5).plusMinutes(59));

        when(studentService.isLoggedIn(student.getId())).thenReturn(true);
        when(lessonRepository.findLessonsByDate(lesson.getDate())).thenReturn(lesson);
        when(lessonRepository.findById(lesson.getId())).thenReturn(Optional.of(lesson));
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(teacherRepository.findTeacherBySubjects(DEFAULT_SUBJECT)).thenReturn(Optional.of(teacher));

        //when
        assertThrows(IllegalRemoveSubjectException.class, () -> lessonService.cancelLesson(student.getId(), lesson.getId()));

        //then
        verify(lessonRepository, times(0)).delete(new Lesson());
    }


    @Test
    void shouldChangeLessonTime() {
        //given
        LocalDateTime originalLessonTime = LESSON_TIME;
        LocalDateTime newLessonDate = LESSON_TIME.plusDays(1).withHour(15);
        Student student = UserUtils.buildStudent();
        Teacher teacher = UserUtils.buildTeacher();
        Lesson lesson = UserUtils.buildLesson();
        lesson.setStudent(student);

        Term originalTerm = Term.builder()
                .lessonTime(originalLessonTime)
                .isFree(false)
                .teacher(teacher)
                .subject(DEFAULT_SUBJECT)
                .build();

        Term newTerm = Term.builder()
                .lessonTime(newLessonDate)
                .isFree(false)
                .teacher(teacher)
                .subject(DEFAULT_SUBJECT)
                .build();

        LessonHours originalLessonHour = LessonHours.builder()
                .date(originalLessonTime)
                .subject(DEFAULT_SUBJECT)
                .build();

        LessonHours newLessonHour = LessonHours.builder()
                .date(newLessonDate)
                .subject(DEFAULT_SUBJECT)
                .build();

        List<LessonHours> lessonHours = new ArrayList<>();
        lessonHours.add(originalLessonHour);
        lessonHours.add(newLessonHour);

        List<Term> teacherTerms = new ArrayList<>();
        teacherTerms.add(originalTerm);
        teacherTerms.add(newTerm);

        when(lessonHoursRepository.findAllBySubject(DEFAULT_SUBJECT)).thenReturn(lessonHours);
        when(teacherRepository.findByEmail(teacher.getEmail())).thenReturn(teacher);
        when(termRepository.findByTeacherId(teacher.getId())).thenReturn(Optional.of(teacherTerms));
        when(termRepository.findByLessonTime(originalLessonTime)).thenReturn(teacherTerms.get(1));
        when(lessonRepository.findLessonsByDate(originalLessonTime)).thenReturn(lesson);
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(lessonRepository.findLessonsByStudentId(student.getId())).thenReturn(List.of(lesson));
        when(lessonRepository.findById(lesson.getId())).thenReturn(Optional.of(lesson));


        //when
        List<StudentLessonDto> studentLessonDtoList = lessonService.changeLessonTime(lesson.getId(), newLessonDate);

        //then
        assertEquals(1, studentLessonDtoList.size());
        for (StudentLessonDto studentLessonDto : studentLessonDtoList) {
            assertEquals(lesson.getTeacherEmail(), studentLessonDto.getTeacherEmail());
            assertEquals(lesson.getSubject(), studentLessonDto.getSubject());
            assertTrue(teacher.getSubjects().contains(studentLessonDto.getSubject()));
        }
    }

    @Test
    void shouldThrowDateExceptionDuringAssignLesson() {
        //when
        assertThrows(LessonNotFoundException.class, () -> lessonService.changeLessonTime(null, LESSON_TIME));

        //then
        verify(lessonRepository, times(0)).save(new Lesson());
    }

    @Test
    void shouldThrowNullParameterExceptionDuringChangeStudentLesson() {
        //given
        Lesson lesson = Lesson.builder()
                .id(1L)
                .build();
        when(lessonRepository.findById(lesson.getId())).thenReturn(Optional.of(lesson));

        //when
        assertThrows(NullParameterException.class,
                () -> lessonService.changeLessonTime(1L, null));

        //then
        verify(termRepository, times(0)).save(new Term());
    }

    @Test
    void shouldThrowDateExceptionDuringChangeStudentLesson() {
        //given
        LocalDateTime wrongDate = LocalDateTime.now().withHour(23).withMinute(0).withSecond(0).withNano(0);
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(new Lesson()));

        //when
        assertThrows(DateException.class,
                () -> lessonService.changeLessonTime(1L, wrongDate));

        //then
        verify(termRepository, times(0)).save(new Term());
    }

    @Test
    void shouldThrowIllegalDateChangeExceptionDuringChangingStudentLessonAfterCurrentDate() {
        //given
        LocalDateTime newDate = LESSON_TIME.plusHours(2);

        Lesson lesson = UserUtils.buildLesson();

        LessonHours currentLessonHour = LessonHours.builder()
                .date(LESSON_TIME)
                .subject(DEFAULT_SUBJECT)
                .build();

        LessonHours newLessonHour = LessonHours.builder()
                .date(newDate)
                .subject(DEFAULT_SUBJECT)
                .build();

        List<LessonHours> lessonHoursList = new ArrayList<>();
        lessonHoursList.add(currentLessonHour);
        lessonHoursList.add(newLessonHour);

        when(lessonHoursRepository.findAllBySubject(DEFAULT_SUBJECT)).thenReturn(lessonHoursList);
        when(lessonRepository.findById(lesson.getId())).thenReturn(Optional.of(lesson));

        //when
        assertThrows(IllegalDateChangeException.class,
                () -> lessonService.changeLessonTime(lesson.getId(), newDate));

        //then
        verify(termRepository, times(0)).save(new Term());
    }

    @Test
    void shouldThrowIllegalDateChangeExceptionDuringChangingStudentLessonBeforeCurrentDate() {
        //given
        Teacher teacher = UserUtils.buildTeacher();

        LocalDateTime currentDate = LESSON_TIME;
        LocalDateTime newDate = LESSON_TIME.minusHours(2);

        Term term = Term.builder()
                .lessonTime(currentDate)
                .isFree(false)
                .teacher(teacher)
                .subject(DEFAULT_SUBJECT)
                .build();

        Lesson lesson = UserUtils.buildLesson();

        LessonHours currentLessonHour = LessonHours.builder()
                .date(currentDate)
                .subject(DEFAULT_SUBJECT)
                .build();

        LessonHours newLessonHour = LessonHours.builder()
                .date(newDate)
                .subject(DEFAULT_SUBJECT)
                .build();

        List<Term> termList = new ArrayList<>();
        termList.add(term);

        List<LessonHours> lessonHoursList = new ArrayList<>();
        lessonHoursList.add(currentLessonHour);
        lessonHoursList.add(newLessonHour);

        when(lessonHoursRepository.findAllBySubject(DEFAULT_SUBJECT)).thenReturn(lessonHoursList);
        when(teacherRepository.findByEmail(teacher.getEmail())).thenReturn(teacher);
        when(termRepository.findByTeacherId(teacher.getId())).thenReturn(Optional.of(termList));
        when(lessonRepository.findById(lesson.getId())).thenReturn(Optional.of(lesson));

        //when
        assertThrows(IllegalDateChangeException.class,
                () -> lessonService.changeLessonTime(lesson.getId(), newDate));

        //then
        verify(termRepository, times(0)).save(new Term());

    }
}
