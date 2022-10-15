package tutorschedule.service;

import tutor_schedule.api.model.student.StudentDto;
import tutor_schedule.api.model.teacher.SubjectDto;
import tutor_schedule.api.model.teacher.TeacherDto;
import tutorschedule.dto.LessonDto;
import tutorschedule.entity.Lesson;
import tutorschedule.entity.Student;
import tutorschedule.entity.Teacher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

public class UserUtils {

    private UserUtils() {

    }

    private static final String PASSWORD = "1234";
    private static final LocalDate BIRTHDAY = LocalDate.of(1999, 12, 12);
    private static final LocalDateTime LESSON_TIME = LocalDateTime.now().withHour(13).withMinute(0).withSecond(0).withNano(0);

    public static Student buildStudent() {
        return Student.builder()
                .id(1L)
                .firstName("Marek")
                .lastName("Markiewicz")
                .email("marek.markiewicz@gmail.com")
                .password(PASSWORD)
                .dateOfBirth(BIRTHDAY)
                .build();
    }

    public static Teacher buildTeacher() {
        return Teacher.builder()
                .id(2L)
                .firstName("Jurek")
                .lastName("Jurkiewicz")
                .email("jurek.jurkiewicz@gmail.com")
                .password(PASSWORD)
                .dateOfBirth(BIRTHDAY)
                .subjects(EnumSet.of(SubjectDto.MATHEMATICS))
                .build();
    }

    public static StudentDto buildStudentDto() {
        return StudentDto.builder()
                .firstName(buildStudent().getFirstName())
                .lastName(buildTeacher().getLastName())
                .email(buildStudent().getEmail())
                .password(PASSWORD)
                .dateOfBirth(BIRTHDAY)
                .build();
    }

    public static TeacherDto buildTeacherDto() {
        return TeacherDto.builder()
                .firstName(buildTeacher().getFirstName())
                .lastName(buildTeacher().getLastName())
                .email(buildTeacher().getEmail())
                .password(PASSWORD)
                .dateOfBirth(BIRTHDAY)
                .subjects(EnumSet.of(SubjectDto.MATHEMATICS))
                .build();
    }

    public static Lesson buildLesson() {
        return Lesson.builder()
                .id(1L)
                .subject(SubjectDto.MATHEMATICS)
                .teacherEmail(buildTeacher().getEmail())
                .teacherFirstName(buildTeacher().getFirstName())
                .teacherLastName(buildTeacher().getLastName())
                .date(LESSON_TIME)
                .build();
    }

    public static LessonDto buildLessonDto() {
        return LessonDto.builder()
                .lessonDate(List.of(LESSON_TIME))
                .subject(SubjectDto.MATHEMATICS)
                .email(buildStudent().getEmail())
                .build();
    }
}
