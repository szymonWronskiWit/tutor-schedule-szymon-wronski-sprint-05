package tutorschedule.mapper;

import tutorschedule.dto.StudentLessonDto;
import tutorschedule.entity.Lesson;

import java.util.List;
import java.util.stream.Collectors;

public class LessonConverter {
    private LessonConverter() {

    }

    public static List<StudentLessonDto> toStudentLessonDto(List<Lesson> lessons) {
        return lessons.stream()
                .map(l -> StudentLessonDto.builder()
                        .date(l.getDate())
                        .subject(l.getSubject())
                        .teacherEmail(l.getTeacherEmail())
                        .teacherFirstName(l.getTeacherFirstName())
                        .teacherLastName(l.getTeacherLastName())
                        .build()).collect(Collectors.toList());
    }
}
