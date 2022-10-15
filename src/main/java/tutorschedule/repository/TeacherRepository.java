package tutorschedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tutor_schedule.api.model.teacher.SubjectDto;
import tutorschedule.entity.Teacher;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Teacher findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Teacher> findTeacherBySubjects(SubjectDto subject);
}
