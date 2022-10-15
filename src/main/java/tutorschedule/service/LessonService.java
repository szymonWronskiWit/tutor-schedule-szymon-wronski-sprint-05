package tutorschedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tutor_schedule.api.model.teacher.SubjectDto;
import tutorschedule.dto.LessonDto;
import tutorschedule.dto.StudentLessonDto;
import tutorschedule.entity.Lesson;
import tutorschedule.entity.LessonHours;
import tutorschedule.entity.Student;
import tutorschedule.entity.Teacher;
import tutorschedule.entity.Term;
import tutorschedule.entity.User;
import tutorschedule.exception.CurrentlyDateTakenException;
import tutorschedule.exception.DateException;
import tutorschedule.exception.IllegalDateChangeException;
import tutorschedule.exception.IllegalRemoveSubjectException;
import tutorschedule.exception.LessonNotFoundException;
import tutorschedule.exception.LogInException;
import tutorschedule.exception.NullParameterException;
import tutorschedule.exception.SubjectNotExistException;
import tutorschedule.exception.UserNotFoundException;
import tutorschedule.mapper.LessonConverter;
import tutorschedule.repository.LessonHoursRepository;
import tutorschedule.repository.LessonRepository;
import tutorschedule.repository.StudentRepository;
import tutorschedule.repository.TeacherRepository;
import tutorschedule.repository.TermRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class LessonService {
    private final StudentService studentService;
    private final TeacherRepository teacherRepository;
    private final LessonRepository lessonRepository;
    private final StudentRepository studentRepository;
    private final TermRepository termRepository;
    private final LessonHoursRepository lessonHoursRepository;

    public List<StudentLessonDto> assignLesson(LessonDto lesson) {
        Student student = studentRepository
                .findByEmail(lesson.getEmail())
                .orElseThrow(UserNotFoundException::new);
        if (!studentService.isLoggedIn(student.getId())) {
            log.error("Student is not logged in");
            throw new LogInException();
        }
        return getStudentLessons(student.getId(), lesson.getLessonDate(), lesson.getSubject());
    }

    private List<StudentLessonDto> getStudentLessons(Long studentId, List<LocalDateTime> dates, SubjectDto subject) {
        if (dates == null) {
            log.error("Lessons of given student are empty");
            throw new LessonNotFoundException();
        }
        getDataToCreateTheLesson(dates, subject, studentId);
        List<Lesson> lessonsByStudentId = lessonRepository.findLessonsByStudentId(studentId);
        return LessonConverter.toStudentLessonDto(lessonsByStudentId);
    }

    private void getDataToCreateTheLesson(List<LocalDateTime> dates, SubjectDto subject, Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> {
            log.error("Not found student with given id");
            throw new UserNotFoundException();
        });
        Teacher teacher = teacherRepository.findTeacherBySubjects(subject).orElseThrow(() -> {
            log.error("Not found teacher with given id");
            throw new UserNotFoundException();
        });
        for (LocalDateTime date : dates) {
            createNewLesson(teacher, subject, date, student);
        }
    }

    private void createNewLesson(User teacher, SubjectDto subject, LocalDateTime date, Student student) {
        boolean hasCorrectSubjectName = ((Teacher) teacher).getSubjects().stream()
                .anyMatch(n -> n.equals(subject));
        if (!hasCorrectSubjectName) {
            log.error("Not found subject name equals to name given from student");
            throw new SubjectNotExistException();
        }
        if (!hasCheckLessonTime(List.of(date), subject)) {
            log.error("Given date not within in lesson hours");
            throw new CurrentlyDateTakenException();
        }
        String teacherEmail = teacher.getEmail();
        Lesson lesson = Lesson.builder()
                .subject(subject)
                .teacherEmail(teacherEmail)
                .teacherFirstName(teacher.getFirstName())
                .teacherLastName(teacher.getLastName())
                .student(student)
                .date(date)
                .build();
        lessonRepository.save(lesson);

        LessonHours lessonHour = lessonHoursRepository.findLessonHoursByDateAndSubject(date, subject);
        assignLessonToTeacher(teacherEmail, date, subject);
        lessonHoursRepository.deleteById(lessonHour.getId());
    }

    private void assignLessonToTeacher(String email, LocalDateTime date, SubjectDto subject) {
        if (!hasCheckLessonTime(List.of(date), subject)) {
            log.error("One or more given lessons time is incorrect");
            throw new CurrentlyDateTakenException();
        }
        if (!hasCheckTeacherEmail(email)) {
            log.error("Not found teacher with given email address");
            throw new UserNotFoundException();
        }
        Teacher teacher = teacherRepository.findByEmail(email);
        Term term = Term.builder()
                .lessonTime(date)
                .isFree(false)
                .teacher(teacher)
                .subject(subject)
                .build();
        termRepository.save(term);
    }

    public List<StudentLessonDto> getStudentLessons(Long studentId) {
        List<Lesson> studentLessons = lessonRepository.findLessonsByStudentId(studentId);
        return LessonConverter.toStudentLessonDto(studentLessons);
    }

    private boolean hasCheckTeacherEmail(String teacherEmail) {
        return teacherRepository.existsByEmail(teacherEmail);
    }

    private void removeLesson(Lesson lesson) {
        checkIfItsPossibleToRemoveLesson(lesson.getDate());
        lessonRepository.delete(lesson);
    }

    private void checkIfItsPossibleToRemoveLesson(LocalDateTime date) {
        LocalDateTime now = LocalDateTime.now();
        int hoursDiff = (int) ChronoUnit.HOURS.between(now, date);
        if (hoursDiff < 6) {
            log.error("The given date for cancellation is less than 6 hours, which is impossible");
            throw new IllegalRemoveSubjectException();
        }
    }

    public List<LocalDateTime> cancelLesson(Long studentId, Long lessonId) {
        Lesson lesson = lessonRepository
                .findById(lessonId)
                .orElseThrow(LessonNotFoundException::new);

        validateUser(studentId);
        validateDateToBeRemoved(lesson);
        List<Lesson> lessons = lessonRepository.findLessonsByStudentId(studentId);
        List<LocalDateTime> lessonsDate = new ArrayList<>();
        for (Lesson l : lessons) {
            lessonsDate.add(l.getDate());
        }
        return lessonsDate;
    }

    private void validateUser(Long studentId) {
        Student student = studentRepository
                .findById(studentId)
                .orElseThrow(UserNotFoundException::new);

        if (!studentService.isLoggedIn(student.getId())) {
            log.error("The student wanted to delete the item without being logged in");
            throw new LogInException();
        }
    }

    private void validateDateToBeRemoved(Lesson lesson) {
        if (lesson.getDate() == null) {
            log.error("Date is null");
            throw new DateException();
        }
        Teacher teacher = teacherRepository.findTeacherBySubjects(lesson.getSubject()).orElseThrow(() -> {
            log.error("Not found teacher with given id");
            throw new UserNotFoundException();
        });
        removeLesson(lesson);
        saveTerm(teacher, lesson);
    }

    private void saveTerm(Teacher teacher, Lesson lesson) {
        Term term = Term.builder()
                .lessonTime(lesson.getDate())
                .isFree(false)
                .teacher(teacher)
                .subject(lesson.getSubject())
                .build();
        termRepository.save(term);
    }

    public List<StudentLessonDto> changeLessonTime(Long lessonId, LocalDateTime newDate) {
        Lesson lesson = lessonRepository
                .findById(lessonId)
                .orElseThrow(LessonNotFoundException::new);
        LocalDateTime originalDate = lesson.getDate();
        if (newDate == null) {
            log.error("Date given from user is null");
            throw new NullParameterException();
        }
        if (!hasCheckLessonTime(List.of(newDate), lesson.getSubject())) {
            log.error("Date given from user not within in lesson hours");
            throw new DateException();
        }
        if (newDate.isBefore(originalDate)) {
            changeDateBeforeOriginalDate(newDate, originalDate);
        } else {
            changeDateAfterOriginalDate(newDate, originalDate);
        }
        return changeStudentLessonDate(originalDate, newDate, lesson.getStudent().getId());
    }

    private boolean hasCheckLessonTime(List<LocalDateTime> dates, SubjectDto subject) {
        List<LocalDateTime> possibleLessonTimes = lessonHoursRepository
                .findAllBySubject(subject)
                .stream()
                .map(LessonHours::getDate)
                .toList();
        return new HashSet<>(possibleLessonTimes).containsAll(dates);
    }

    public void generateAllTermsForSubjects() {
        LocalDateTime startDate = LocalDateTime.now().withHour(8).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDate = LocalDateTime.now().withHour(18).withMinute(0).withSecond(0).withNano(0);
        endDate = endDate.plusDays(7);

        List<LessonHours> lessonHours = new ArrayList<>();
        boolean isStartDateBeforeEndDate = startDate.isBefore(endDate);
        while (isStartDateBeforeEndDate) {
            for (SubjectDto s : SubjectDto.values()) {
                LessonHours lessonHour = LessonHours.builder()
                        .date(startDate)
                        .subject(s)
                        .build();
                lessonHours.add(lessonHour);
            }
            startDate = startDate.plusMinutes(30);
            if (startDate.isEqual(endDate)) {
                isStartDateBeforeEndDate = false;
            }
            if (startDate.getHour() == endDate.getHour()) {
                startDate = startDate.plusDays(1).withHour(8).withMinute(0);
            }
        }
        lessonHoursRepository.saveAll(lessonHours);
    }

    private void changeDateBeforeOriginalDate(LocalDateTime newDate, LocalDateTime originalDate) {
        LocalDateTime currentDate = LocalDateTime.now().withSecond(0).withNano(0);
        long changeBeforeCurrentDate = ChronoUnit.HOURS.between(currentDate, newDate);
        if (changeBeforeCurrentDate < 12) {
            log.error("User try to change lesson time less then 12 hours before lesson");
            throw new IllegalDateChangeException();
        }
        changeTerm(newDate, originalDate);
    }

    private void changeDateAfterOriginalDate(LocalDateTime newDate, LocalDateTime originalDate) {
        long changeAfterCurrentDate = ChronoUnit.HOURS.between(originalDate, newDate);
        if (changeAfterCurrentDate < 12) {
            log.error("User try to change lesson time less then 12 hours before lesson");
            throw new IllegalDateChangeException();
        }
        changeTerm(newDate, originalDate);
    }

    private void changeTerm(LocalDateTime newDate, LocalDateTime originalDate) {
        Term term = termRepository.findByLessonTime(originalDate);
        term.setLessonTime(newDate);
        termRepository.save(term);
    }

    private List<StudentLessonDto> changeStudentLessonDate(LocalDateTime originalDate,
                                                           LocalDateTime newDate,
                                                           Long studentId) {

        Lesson lesson = lessonRepository.findLessonsByDate(originalDate);
        lesson.setDate(newDate);
        lessonRepository.save(lesson);

        List<Lesson> lessons = lessonRepository.findLessonsByStudentId(studentId);
        return LessonConverter.toStudentLessonDto(lessons);
    }
}
