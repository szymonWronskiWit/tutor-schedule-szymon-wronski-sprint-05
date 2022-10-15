package tutorschedule.config;

import lombok.RequiredArgsConstructor;
import tutorschedule.service.LessonService;

@RequiredArgsConstructor
public class RunnableTask implements Runnable {

    private final LessonService lessonService;

    @Override
    public void run() {
        lessonService.generateAllTermsForSubjects();
    }
}
