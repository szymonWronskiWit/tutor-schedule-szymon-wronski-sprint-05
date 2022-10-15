package tutorschedule.api;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutor_schedule.api.lesson.DefaultApi;
import tutorschedule.dto.LessonDto;
import tutorschedule.dto.StudentLessonDto;
import tutorschedule.service.LessonService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/lessons")
public class LessonApiImpl implements DefaultApi {

    private final LessonService lessonService;

    @PostMapping
    public ResponseEntity<List<StudentLessonDto>> assignLesson(@RequestBody LessonDto lessonDto) {
        return ResponseEntity.ok().body(lessonService.assignLesson(lessonDto));
    }

    @DeleteMapping
    public ResponseEntity<List<LocalDateTime>> removeLesson(@RequestHeader(value = "Authentication") Long studentId, @RequestParam Long lessonId) {
        return ResponseEntity.ok().body(lessonService.cancelLesson(studentId, lessonId));
    }

    @PatchMapping(value = "/changeLessonDate/{lessonId}")
    public ResponseEntity<String> changeLessonDate(@PathVariable Long lessonId,
                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDate) {
        lessonService.changeLessonTime(lessonId, newDate);
        return ResponseEntity.ok().body("Date change successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<StudentLessonDto>> studentLessons(@PathVariable Long id) {
        return ResponseEntity.ok().body(lessonService.getStudentLessons(id));
    }
}
