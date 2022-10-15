package tutorschedule.api;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutor_schedule.api.model.teacher.SubjectDto;
import tutor_schedule.api.term.DefaultApi;
import tutorschedule.service.TermService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/terms")
public class TermApiImpl implements DefaultApi {
    private final TermService termService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<List<LocalDateTime>> getTeacherTerms(@PathVariable Long id) {
        return ResponseEntity.ok().body(termService.teacherTerms(id));
    }

    @GetMapping
    public ResponseEntity<List<LocalDateTime>> lessonTerm(@RequestParam boolean isFree,
                                                          @RequestParam SubjectDto subject,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        if (isFree) {
            return ResponseEntity.ok().body(termService.getFreeTerms(subject, date));
        }
        return ResponseEntity.ok().body(termService.getBusyTerms(subject));
    }
}
