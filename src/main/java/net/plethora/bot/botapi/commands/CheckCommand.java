package net.plethora.bot.botapi.commands;

import net.plethora.bot.botapi.keyboards.KeyboardCmdMenu;
import net.plethora.bot.botapi.state.BotState;
import net.plethora.bot.botapi.system.systemMessage.AgeOptionBookMessage;
import net.plethora.bot.botapi.system.systemMessage.OptionTypeTaskMessage;
import net.plethora.bot.dao.DataAccessUser;
import net.plethora.bot.model.User;
import net.plethora.bot.service.PhrasesService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

@Component
public class CheckCommand<T> {

    private OptionTypeTaskMessage optionTypeTaskMessage;
    private AgeOptionBookMessage ageOptionBookMessage;
    private KeyboardCmdMenu keyboardCmdMenu;

    public CheckCommand(OptionTypeTaskMessage optionTypeTaskMessage, AgeOptionBookMessage ageOptionBookMessage, KeyboardCmdMenu keyboardCmdMenu) {
        this.optionTypeTaskMessage = optionTypeTaskMessage;
        this.ageOptionBookMessage = ageOptionBookMessage;
        this.keyboardCmdMenu = keyboardCmdMenu;
    }

    /**
     * Проверка на ввод команд
     *
     * @param chatId  id чата пользователя
     * @param askUser сообщение пользователя
     * @return готовое телеграм сообщение к отправке
     */
    public List<T> inspect(long chatId, String askUser, User user, DataAccessUser dataAccessUser, PhrasesService phrases) {
        List<T> messages = new ArrayList<>();
        switch (askUser) {
            case Cmd.START:
                if (user.getState() != null) {
                    dataAccessUser.editUser(user, null);
                }
                messages.add((T) new SendMessage(chatId, phrases.getMessage("phrase.NeedEnableService")));
                break;

            case Cmd.MENU:
                messages.add((T) keyboardCmdMenu.process(chatId));
                break;   //открываем меню

            case Cmd.HELP:
            case Cmd.HELP_BUTTON:
                messages.add((T) new SendMessage(chatId, phrases.getMessage("phrase.help")));
                break;

            case Cmd.ASK:
            case Cmd.ASK_BUTTON:    //Состояние вопрос-ответ
                dataAccessUser.editUser(user, BotState.ASK);
                messages.add((T) new SendMessage(chatId, phrases.getMessage("phrase.AskEnableService")));
                break;

            case Cmd.TASK:
            case Cmd.TASK_BUTTON:   //Состояние задача
                dataAccessUser.editUser(user, BotState.TASK);
                messages.add((T) optionTypeTaskMessage.message(chatId));
                break;

            case Cmd.JOB:
            case Cmd.JOB_BUTTON:    //Состояние поиск работы
                dataAccessUser.editUser(user, BotState.JOB);
                messages.add((T) new SendMessage(chatId, phrases.getMessage("phrase.JobEnableService")));
                break;

            case Cmd.BOOK:
            case Cmd.BOOK_BUTTON:
                dataAccessUser.editUser(user, BotState.BOOK);
                messages.add((T) ageOptionBookMessage.message(chatId));
                break;
        }
        return messages;
    }
}