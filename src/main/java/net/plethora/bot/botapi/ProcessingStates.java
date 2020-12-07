package net.plethora.bot.botapi;

import net.plethora.bot.botapi.state.BotState;
import net.plethora.bot.service.*;
import net.plethora.bot.service.menu.*;
import org.springframework.stereotype.Component;


@Component
public class ProcessingStates {

    private AskServiceMenu askService;
    private TaskServiceMenu taskService;
    private JobServiceMenu jobService;
    private BookServiceMenu bookServiceMenu;
    private QuizServiceMenu quizServiceMenu;

    public ProcessingStates(AskServiceMenu askService, TaskServiceMenu taskService, JobServiceMenu jobService, BookServiceMenu bookServiceMenu,QuizServiceMenu quizServiceMenu) {
        this.askService = askService;
        this.taskService = taskService;
        this.jobService = jobService;
        this.bookServiceMenu = bookServiceMenu;
        this.quizServiceMenu = quizServiceMenu;
    }

    /**
     * Определяем состояние
     *
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
            case QUIZ:
                return quizServiceMenu;

        }
        return null;
    }
}