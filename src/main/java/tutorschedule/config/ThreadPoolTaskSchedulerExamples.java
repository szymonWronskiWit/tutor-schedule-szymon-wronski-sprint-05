package tutorschedule.config;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import tutorschedule.service.LessonService;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class ThreadPoolTaskSchedulerExamples {

    private final ThreadPoolTaskScheduler taskScheduler;

    private final CronTrigger cronTrigger;

    private final LessonService lessonService;

    @PostConstruct
    public void scheduleRunnableWithCronTrigger() {
        taskScheduler.schedule(new RunnableTask(lessonService), cronTrigger);
    }
}
