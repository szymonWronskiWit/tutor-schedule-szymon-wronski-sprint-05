package tutorschedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tutor_schedule.api.model.teacher.SubjectDto;
import tutorschedule.entity.LessonHours;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LessonHoursRepository extends JpaRepository<LessonHours, Long> {
    List<LessonHours> findAllBySubject(SubjectDto subject);
    LessonHours findLessonHoursByDateAndSubject(LocalDateTime date, SubjectDto subject);
}
