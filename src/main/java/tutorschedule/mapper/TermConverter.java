package tutorschedule.mapper;

import tutorschedule.entity.Term;

import java.time.LocalDateTime;
import java.util.List;

public class TermConverter {
    private TermConverter() {

    }

    public static List<LocalDateTime> getLessonTime(List<Term> terms) {
        return terms.stream()
                .map(Term::getLessonTime)
                .toList();
    }
}
