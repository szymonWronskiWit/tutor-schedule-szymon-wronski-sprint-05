package tutorschedule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tutor_schedule.api.model.teacher.SubjectDto;
import tutorschedule.entity.LessonHours;
import tutorschedule.entity.Teacher;
import tutorschedule.entity.Term;
import tutorschedule.exception.DateException;
import tutorschedule.exception.NullParameterException;
import tutorschedule.exception.TermsNotFoundException;
import tutorschedule.repository.LessonHoursRepository;
import tutorschedule.repository.TermRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TermServiceTest {

    @MockBean
    private TermRepository termRepository;
    @MockBean
    private LessonHoursRepository lessonHoursRepository;
    private TermService termService;


    @BeforeEach
    void setup() {
        termService = new TermService(termRepository, lessonHoursRepository);
    }

    private static final LocalDateTime LESSON_TIME = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0).withNano(0);

    private static final SubjectDto DEFAULT_SUBJECT = SubjectDto.MATHEMATICS;

    @Test
    void shouldReturnTeacherTerms() {
        //given
        Teacher teacher = UserUtils.buildTeacher();
        Term term = Term.builder()
                .id(1L)
                .lessonTime(LESSON_TIME)
                .isFree(false)
                .build();

        when(termRepository.findByTeacherId(teacher.getId())).thenReturn(Optional.of(List.of(term)));

        //when
        List<LocalDateTime> teacherTerms = termService.teacherTerms(teacher.getId());

        //then
        assertEquals(1, teacherTerms.size());
    }

    @Test
    void shouldThrowExceptionWhenTeacherHaveNotGotTerms() {
        //given
        Teacher teacher = UserUtils.buildTeacher();

        when(termRepository.findByTeacherId(teacher.getId())).thenReturn(Optional.empty());

        //when
        assertThrows(TermsNotFoundException.class, () -> termService.teacherTerms(teacher.getId()));
    }

    @Test
    void shouldGetFreeLessonsTerms() {
        //given
        LocalDateTime termsForTodayDate = LocalDateTime.now();
        LocalDateTime termsForSelectedDate = LocalDateTime.now().plusDays(2);
        LocalDateTime allFreeLessonTerms = LocalDateTime.now().plusDays(9);

        List<LessonHours> lessonHoursList = new ArrayList<>();
        LocalDateTime date = LocalDateTime.now();
        for (int i = 0; i <= 5; i++) {
            LessonHours lessonHours = LessonHours.builder()
                    .date(date)
                    .subject(DEFAULT_SUBJECT)
                    .build();
            lessonHoursList.add(lessonHours);
            date = date.plusDays(1);
        }

        List<LessonHours> todayTermsExample = lessonHoursList
                .stream()
                .filter(l -> l.getDate().getDayOfMonth() == LocalDateTime.now().getDayOfMonth())
                .toList();

        List<LessonHours> selectedTermsExample = lessonHoursList
                .stream()
                .filter(l -> l.getDate().getDayOfMonth() == LocalDateTime.now().plusDays(1).getDayOfMonth())
                .toList();

        when(lessonHoursRepository.findAllBySubject(DEFAULT_SUBJECT)).thenReturn(lessonHoursList);

        //when
        List<LocalDateTime> termsForToday = termService.getFreeTerms(DEFAULT_SUBJECT, termsForTodayDate);
        List<LocalDateTime> selectedTerms = termService.getFreeTerms(DEFAULT_SUBJECT, termsForSelectedDate);
        List<LocalDateTime> allTerms = termService.getFreeTerms(DEFAULT_SUBJECT, allFreeLessonTerms);


        //then
        assertNotNull(termsForToday);
        assertNotNull(selectedTerms);
        assertNotNull(allTerms);
        assertEquals(termsForToday.size(), selectedTerms.size());
        assertEquals(todayTermsExample.size(), termsForToday.size());
        assertEquals(selectedTermsExample.size(), selectedTerms.size());
        assertEquals(lessonHoursList.size(), allTerms.size());
    }

    @Test
    void shouldThrowDateExceptionDuringGettingFreeTerms() {
        //when
        assertThrows(DateException.class, () -> termService.getFreeTerms(DEFAULT_SUBJECT, null));

        //then
        verify(lessonHoursRepository, times(0)).findAllBySubject(null);
    }

    @Test
    void shouldThrowNullParameterExceptionDuringGettingFreeTerms() {
        //given
        LocalDateTime date = LocalDateTime.now();

        //when
        assertThrows(NullParameterException.class, () -> termService.getFreeTerms(null, date));

        //then
        verify(lessonHoursRepository, times(0)).findAllBySubject(null);
    }
}
