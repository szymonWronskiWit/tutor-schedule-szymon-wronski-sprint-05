package tutorschedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tutorschedule.entity.Lesson;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findLessonsByStudentId(Long id);
    Lesson findLessonsByDate(LocalDateTime date);
}
