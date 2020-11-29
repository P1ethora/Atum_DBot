package net.plethora.bot.botapi;

import net.plethora.bot.botapi.state.BotState;
import net.plethora.bot.service.*;
import org.springframework.stereotype.Component;


@Component
public class ProcessingStates {

    private AskServiceMenu askService;
    private TaskServiceMenu taskService;
    private JobServiceMenu jobService;

    public ProcessingStates(AskServiceMenu askService, TaskServiceMenu taskService, JobServiceMenu jobService) {
        this.askService = askService;
        this.taskService = taskService;
        this.jobService = jobService;
    }

    /**
     * Определяем состояние
     * @param botState Enum состояние
     * @return сервис
     */
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