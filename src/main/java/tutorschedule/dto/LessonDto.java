package tutorschedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutor_schedule.api.model.teacher.SubjectDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private List<LocalDateTime> lessonDate;
    private SubjectDto subject;
    private String email;
}
