package net.plethora.bot.botapi;

import net.plethora.bot.botapi.state.BotState;
import net.plethora.bot.service.*;
import net.plethora.bot.service.menu.AskServiceMenu;
import net.plethora.bot.service.menu.BookServiceMenu;
import net.plethora.bot.service.menu.JobServiceMenu;
import net.plethora.bot.service.menu.TaskServiceMenu;
import org.springframework.stereotype.Component;


@Component
public class ProcessingStates {

    private AskServiceMenu askService;
    private TaskServiceMenu taskService;
    private JobServiceMenu jobService;
    private BookServiceMenu bookServiceMenu;

    public ProcessingStates(AskServiceMenu askService, TaskServiceMenu taskService, JobServiceMenu jobService,BookServiceMenu bookServiceMenu) {
        this.askService = askService;
        this.taskService = taskService;
        this.jobService = jobService;
        this.bookServiceMenu = bookServiceMenu;
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
            case BOOK:
                return bookServiceMenu;

        }
        return null;
    }
}