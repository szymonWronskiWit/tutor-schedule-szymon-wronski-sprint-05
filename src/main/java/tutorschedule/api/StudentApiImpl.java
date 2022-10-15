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
import org.springframework.web.bind.annotation.RestController;
import tutor_schedule.api.model.student.StudentDto;
import tutor_schedule.api.student.DefaultApi;
import tutorschedule.exception.UserNotFoundException;
import tutorschedule.mapper.UserConverter;
import tutorschedule.repository.StudentRepository;
import tutorschedule.service.StudentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentApiImpl implements DefaultApi {

    private final StudentService studentService;
    private final StudentRepository studentRepository;

    @PostMapping
    public ResponseEntity<StudentDto> add(@RequestBody StudentDto student) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.addStudent(student));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<StudentDto> get(@PathVariable Long id) {
        return ResponseEntity.ok().body(
                UserConverter.toStudentDto(
                        studentRepository
                                .findById(id)
                                .orElseThrow(UserNotFoundException::new)));
    }

    @GetMapping
    public ResponseEntity<List<StudentDto>> getAll() {
        return ResponseEntity.ok().body(studentService.getStudents());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().body("Deleted successfully");
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable Long id, @RequestBody StudentDto student) {
        return ResponseEntity.ok().body(studentService.updateStudent(id, student));
    }
}
