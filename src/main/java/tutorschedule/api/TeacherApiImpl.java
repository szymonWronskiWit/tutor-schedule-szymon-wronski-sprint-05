package tutorschedule.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutor_schedule.api.model.teacher.SubjectDto;
import tutor_schedule.api.model.teacher.TeacherDto;
import tutor_schedule.api.teacher.DefaultApi;
import tutorschedule.exception.UserNotFoundException;
import tutorschedule.mapper.UserConverter;
import tutorschedule.repository.TeacherRepository;
import tutorschedule.service.TeacherService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeacherApiImpl implements DefaultApi {
    private final TeacherService teacherService;
    private final TeacherRepository teacherRepository;

    @PostMapping
    public ResponseEntity<TeacherDto> add(@RequestBody TeacherDto teacher) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.addTeacher(teacher));
    }

    @GetMapping
    public ResponseEntity<List<TeacherDto>> getAll() {
        return ResponseEntity.ok().body(teacherService.getAllTeachers());
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<TeacherDto> update(@PathVariable Long id, @RequestBody TeacherDto teacher) {
        return ResponseEntity.ok().body(teacherService.updateTeacher(id, teacher));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.ok().body("Deleted successfully");
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<TeacherDto> get(@PathVariable Long id) {
        return ResponseEntity.ok().body(
                UserConverter.toTeacherDto(
                        teacherRepository
                                .findById(id)
                                .orElseThrow(UserNotFoundException::new)));
    }

    @PostMapping(value = "{id}/{subject}")
    public ResponseEntity<Set<SubjectDto>> addSubject(@PathVariable SubjectDto subject, @PathVariable Long id) {
        return ResponseEntity.ok().body(teacherService.addSubject(id, subject));
    }

    @DeleteMapping(value = "{id}/subjects/{subject}")
    public ResponseEntity<String> removeSubject(@PathVariable SubjectDto subject, @PathVariable Long id) {
        teacherService.removeSubject(id, subject);
        return ResponseEntity.ok().body("Removed successfully");
    }

    @GetMapping(value = "{id}/subjects")
    public ResponseEntity<List<LocalDateTime>> allLessonDates(@RequestParam SubjectDto subject, @PathVariable Long id) {
        return ResponseEntity.ok().body(teacherService.getAllLessonDates(id, subject));
    }
}
