package tutorschedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tutor_schedule.api.model.teacher.SubjectDto;
import tutorschedule.entity.LessonHours;
import tutorschedule.entity.Term;
import tutorschedule.exception.DateException;
import tutorschedule.exception.NullParameterException;
import tutorschedule.exception.SubjectNotExistException;
import tutorschedule.exception.TermsNotFoundException;
import tutorschedule.mapper.TermConverter;
import tutorschedule.repository.LessonHoursRepository;
import tutorschedule.repository.TermRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class TermService {
    private final TermRepository termRepository;
    private final LessonHoursRepository lessonHoursRepository;

    public List<LocalDateTime> teacherTerms(long id) {
        List<Term> terms = termRepository.findByTeacherId(id)
                .orElseThrow(TermsNotFoundException::new);
        return TermConverter.getLessonTime(terms);
    }

    public List<LocalDateTime> getFreeTerms(SubjectDto subject, LocalDateTime date) {
        boolean isCorrectSubject = Arrays.asList(SubjectDto.values()).contains(subject);
        if (date == null) {
            log.error("Date is null");
            throw new DateException();
        }
        if (date.isBefore(LocalDateTime.now().minusMinutes(1))) {
            log.error("You cannot enroll in a lesson in the past");
            throw new DateException();
        }
        if (subject == null) {
            log.error("subject is null");
            throw new NullParameterException();
        }
        if (!isCorrectSubject) {
            log.error("Subject is invalid");
            throw new SubjectNotExistException();
        }
        List<LessonHours> lessonHours = lessonHoursRepository.findAllBySubject(subject);
        List<LocalDateTime> allTermsDateBySubject = lessonHours.stream()
                .map(LessonHours::getDate)
                .toList();
        if (date.getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
            return todayTerm(allTermsDateBySubject);
        } else if (date.isBefore(LocalDateTime.now().plusDays(7))) {
            return enteredTerm(date, allTermsDateBySubject);
        }
        return allTermsDateBySubject;
    }

    public List<LocalDateTime> getBusyTerms(SubjectDto subject) {
        List<Term> terms = termRepository.findAllBySubject(subject)
                .orElseThrow(TermsNotFoundException::new);
        return terms.stream()
                .map(Term::getLessonTime)
                .toList();
    }

    private List<LocalDateTime> todayTerm(List<LocalDateTime> lessonTermsBySubject) {
        LocalDateTime now = LocalDateTime.now();
        lessonTermsBySubject = lessonTermsBySubject.stream()
                .filter(term -> term.isAfter(now.withHour(7)) && term.isBefore(now.withHour(19)))
                .collect(Collectors.toList());
        return lessonTermsBySubject;
    }

    private List<LocalDateTime> enteredTerm(LocalDateTime date, List<LocalDateTime> lessonTermsBySubject) {
        lessonTermsBySubject = lessonTermsBySubject.stream()
                .filter(term -> term.isAfter(date.withHour(7)) && term.isBefore(date.withHour(19)))
                .collect(Collectors.toList());
        return lessonTermsBySubject;
    }
}
