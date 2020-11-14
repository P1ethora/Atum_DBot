package net.plethora.bot.botapi;

import net.plethora.bot.service.*;
import org.springframework.stereotype.Component;


@Component
public class ProcessingStates {

    private AskServiceMenu askService;
    private TaskServiceMenu taskService;
    private JobServiceMenu jobService;

    /**
     * Определяем состояние и возвращаем сервис
     */
    public ProcessingStates(AskServiceMenu askService, TaskServiceMenu taskService, JobServiceMenu jobService) {
        this.askService = askService;
        this.taskService = taskService;
        this.jobService = jobService;
    }

    ServiceMenu processing(BotState botState) {

        switch (botState) {
            case ASK:
                return askService;
            case TASK:
                return taskService;
            case JOB:
                return jobService;

        }
        return null;
    }
}