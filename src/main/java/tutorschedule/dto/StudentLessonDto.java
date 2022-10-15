package tutorschedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutor_schedule.api.model.teacher.SubjectDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentLessonDto {
    private LocalDateTime date;
    private SubjectDto subject;
    private String teacherEmail;
    private String teacherFirstName;
    private String teacherLastName;
}
