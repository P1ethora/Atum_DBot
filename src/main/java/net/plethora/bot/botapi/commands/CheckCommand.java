package net.plethora.bot.botapi.commands;

import net.plethora.bot.botapi.keyboards.KeyboardCmdMenu;
import net.plethora.bot.botapi.keyboards.kbquiz.KeyboardSoloButtonQuiz;
import net.plethora.bot.botapi.state.BotState;
import net.plethora.bot.botapi.state.SubState;
import net.plethora.bot.botapi.system.systemMessage.AgeOptionBookMessage;
import net.plethora.bot.botapi.system.systemMessage.OptionTypeTaskMessage;
import net.plethora.bot.botapi.system.systemMessage.SearchDataJobMessage;
import net.plethora.bot.dao.DataAccessUser;
import net.plethora.bot.model.UserTelegram;
import net.plethora.bot.model.systemmodel.InfoForSearch;
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
    private KeyboardSoloButtonQuiz keyboardSoloButtonQuiz;
    private SearchDataJobMessage searchDataJobMessage;

    public CheckCommand(OptionTypeTaskMessage optionTypeTaskMessage, AgeOptionBookMessage ageOptionBookMessage,
                        KeyboardCmdMenu keyboardCmdMenu, KeyboardSoloButtonQuiz keyboardSoloButtonQuiz,
                        SearchDataJobMessage searchDataJobMessage) {
        this.optionTypeTaskMessage = optionTypeTaskMessage;
        this.ageOptionBookMessage = ageOptionBookMessage;
        this.keyboardCmdMenu = keyboardCmdMenu;
        this.keyboardSoloButtonQuiz = keyboardSoloButtonQuiz;
        this.searchDataJobMessage = searchDataJobMessage;
    }

    /**
     * Проверка на ввод команд
     *
     * @param chatId  id чата пользователя
     * @param askUser сообщение пользователя
     * @return готовое телеграм сообщение к отправке
     */
    public List<T> inspect(long chatId, String askUser, UserTelegram userTelegram, DataAccessUser dataAccessUser, PhrasesService phrases) {
        List<T> messages = new ArrayList<>();
        switch (askUser) {
            case Cmd.START:
                if (userTelegram.getState() != null) {
                    //TODO возможно стоит сделать общий метод
                    BotState botState = null;
                    SubState subState = null;
                    dataAccessUser.editUser(userTelegram, botState);
                    dataAccessUser.editUser(userTelegram, subState);
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
                dataAccessUser.editUser(userTelegram, BotState.ASK);
                messages.add((T) new SendMessage(chatId, phrases.getMessage("phrase.AskEnableService")));
                break;

            case Cmd.TASK:
            case Cmd.TASK_BUTTON:   //Состояние задача
                dataAccessUser.editUser(userTelegram, BotState.TASK);
                messages.add((T) optionTypeTaskMessage.message(chatId));
                break;

            case Cmd.JOB:
            case Cmd.JOB_BUTTON:    //Состояние поиск работы
                dataAccessUser.editUser(userTelegram, BotState.JOB);
                messages.add((T) searchDataJobMessage.message(chatId,new InfoForSearch(0,chatId,null,null,0)));
                break;

            case Cmd.BOOK:
            case Cmd.BOOK_BUTTON:
                dataAccessUser.editUser(userTelegram, BotState.BOOK);
                messages.add((T) ageOptionBookMessage.message(chatId));
                break;

            case Cmd.QUIZ:
            case Cmd.QUIZ_BUTTON:
                dataAccessUser.editUser(userTelegram, BotState.QUIZ);
                messages.add((T) new SendMessage(chatId, "Сервис QUIZ подключен")
                        .setReplyMarkup(keyboardSoloButtonQuiz.keyboardStart()));
                break;
        }
        return messages;
    }
}