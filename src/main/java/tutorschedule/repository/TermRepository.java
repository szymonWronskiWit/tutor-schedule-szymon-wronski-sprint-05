package tutorschedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tutor_schedule.api.model.teacher.SubjectDto;
import tutorschedule.entity.Term;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {
    Term findByLessonTime(LocalDateTime date);
    Optional<List<Term>> findByTeacherId(Long id);
    Optional<List<Term>> findAllBySubject(SubjectDto subject);

}
