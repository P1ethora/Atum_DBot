package net.plethora.bot.botapi;

import net.plethora.bot.service.*;
import org.springframework.stereotype.Component;


@Component
public class ProcessingStates {

    private AskService askService;
    private TaskService taskService;
    private JobService jobService;

    /**
     * Определяем состояние и возвращаем сервис
     */
    public ProcessingStates(AskService askService, TaskService taskService, JobService jobService) {
        this.askService = askService;
        this.taskService = taskService;
        this.jobService = jobService;
    }

    Service processing(BotState botState) {

        switch (botState) {
            case ASK:
                return askService;
            case TASKS:
                return taskService;
            case JOBS:
                return jobService;

        }
        return null;
    }
}