package net.plethora.bot.botapi;

import net.plethora.bot.botapi.commands.Cmd;
import net.plethora.bot.model.system.SubjectTaskUser;
import net.plethora.bot.botapi.keyboards.KeyboardCmdMenu;
import net.plethora.bot.botapi.keyboards.KeyboardSubjectTask;
import net.plethora.bot.botapi.state.BotState;
import net.plethora.bot.cache.CacheUsersState;
import net.plethora.bot.dao.DataAccessUser;
import net.plethora.bot.model.User;
import net.plethora.bot.service.PhrasesService;
import net.plethora.bot.systemMessage.AgeOptionBookMessage;
import net.plethora.bot.systemMessage.OptionTypeTaskMessage;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
public class BotExecution<T> {

    private CacheUsersState cacheUsersState;
    private ProcessingStates processingStates;
    private KeyboardCmdMenu keyboardCmdMenu;
    private PhrasesService phrases;
    private DataAccessUser dataAccessUser;

    private OptionTypeTaskMessage optionTypeTaskMessage;
    private AgeOptionBookMessage ageOptionBookMessage;

    private User user;

    public BotExecution(CacheUsersState cacheUsersState, ProcessingStates processingStates, KeyboardCmdMenu keyboardCmdMenu, PhrasesService phrases, DataAccessUser dataAccessUser, AgeOptionBookMessage ageOptionBookMessage, OptionTypeTaskMessage optionTypeTaskMessage) {
        this.cacheUsersState = cacheUsersState;
        this.processingStates = processingStates;
        this.keyboardCmdMenu = keyboardCmdMenu;
        this.phrases = phrases;
        this.dataAccessUser = dataAccessUser;
        this.ageOptionBookMessage = ageOptionBookMessage;
        this.optionTypeTaskMessage = optionTypeTaskMessage;
    }

    /**
     * Исполнение запроса: сообщение(hasText) или кнопка(hasCallbackQuery)
     *
     * @param update пакет от пользователя
     * @return телеграмм сообщение для пользователя
     */
    public List<T> process(Update update) {

        long chatId;
        String askUser;
        List<T> messages = new ArrayList<>();
//------------------------------------------------------------------------------------------------//
        //КНОПКА
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();  //данные нажатия кнопки
            chatId = callbackQuery.getMessage().getChatId();          //id чата кнопки
            askUser = callbackQuery.getData().toLowerCase();          //содержимое кнопки(переводим в нижни реестр)
            String callbackQueryId = callbackQuery.getId();      //id callback для всплывающего окна
            int messageId = callbackQuery.getMessage().getMessageId();

            messages = checkCommand(chatId, askUser);
            if (messages.size() == 0) {     //является ли командой

                String firstName = update.getCallbackQuery().getFrom().getFirstName();//<---------------
                String lastName = update.getCallbackQuery().getFrom().getLastName();  //   Данные      |
                String userName = update.getCallbackQuery().getFrom().getUserName();  //  Пользователя |
                int idUser = update.getCallbackQuery().getFrom().getId();             //<---------------

                user = checkUser(chatId, idUser, firstName, lastName, userName);
                //включаем сервис
                messages = enabledService(chatId, askUser, messageId, callbackQueryId);
                return messages;
            }
//-----------------------------------------------------------------------------------------------------//
            //СООБЩЕНИЕ
        } else if (update.getMessage() != null && update.getMessage().hasText()) {  //если отправленно сообщение
            chatId = update.getMessage().getChatId();                //id чата
            askUser = update.getMessage().getText().toLowerCase();   //запрос пользователя

            String firstName = update.getMessage().getFrom().getFirstName();//<---------------
            String lastName = update.getMessage().getFrom().getLastName();  //   Данные      |
            String userName = update.getMessage().getFrom().getUserName();  //  Пользователя |
            int idUser = update.getMessage().getFrom().getId();             //<---------------

            user = checkUser(chatId, idUser, firstName, lastName, userName);
            //Сперва проверяются команды
            messages = checkCommand(chatId, askUser);   //запрос -> команда
            if (messages.size() == 0) {                      //Если не является командой, запрос согласно сервиса меню
                messages = enabledService(chatId, askUser, 0, null);

            }
        }
//---------------------------------------------------------------------------------------------------------//
        return messages;
    }

    /**
     * Проверка на ввод команд
     *
     * @param chatId  id чата пользователя
     * @param askUser сообщение пользователя
     * @return готовое телеграм сообщение к отправке
     */
    private List<T> checkCommand(long chatId, String askUser) {
        List<T> messages = new ArrayList<>();
        switch (askUser) {
            case Cmd.START: {
                if (cacheUsersState.getStateUsers().get(chatId) != null) {
                    cacheUsersState.getStateUsers().remove(chatId); //переходим в неопределенное сотояние
                }
                messages.add((T) new SendMessage(chatId, phrases.getMessage("phrase.NeedEnableService")));
                break;

            }
            case Cmd.MENU: {
                messages.add((T) keyboardCmdMenu.process(chatId));
                break;   //открываем меню

            }
            case Cmd.HELP:
            case Cmd.HELP_BUTTON: {
                messages.add((T) new SendMessage(chatId, phrases.getMessage("phrase.help")));
                break;

            }
            case Cmd.ASK:
            case Cmd.ASK_BUTTON: {   //Состояние вопрос-ответ
                dataAccessUser.editUser(user, BotState.ASK);
                cacheUsersState.getStateUsers().put(chatId, BotState.ASK);
                messages.add((T) new SendMessage(chatId, phrases.getMessage("phrase.AskEnableService")));
                break;

            }
            case Cmd.TASK:
            case Cmd.TASK_BUTTON: {   //Состояние задача
                dataAccessUser.editUser(user, BotState.TASK);
                cacheUsersState.getStateUsers().put(chatId, BotState.TASK);
                messages.add((T) optionTypeTaskMessage.message(chatId));
                break;

            }
            case Cmd.JOB:
            case Cmd.JOB_BUTTON: {   //Состояние поиск работы
                dataAccessUser.editUser(user, BotState.JOB);
                cacheUsersState.getStateUsers().put(chatId, BotState.JOB);
                messages.add((T) new SendMessage(chatId, phrases.getMessage("phrase.JobEnableService")));
                break;
            }
            case Cmd.BOOK:
                dataAccessUser.editUser(user, BotState.BOOK);
                cacheUsersState.getStateUsers().put(chatId, BotState.BOOK);
                messages.add((T) ageOptionBookMessage.message(chatId));
                break;
        }

        return messages;
    }

    /**
     * Включаем сервис или отправляем сообщение с запросом включить сервис
     * Параметры из пакета update
     *
     * @param chatId  id чата
     * @param askUser сообщение пользователя
     * @return список с сообщениями
     */
    private List<T> enabledService(long chatId, String askUser, int messageId, String inlineMessageId) {
        List<T> messages = new ArrayList<>();
        if (cacheUsersState.getStateUsers().get(chatId) != null) {//Если активировано состояние

            messages = processingStates.processing(cacheUsersState.getStateUsers().get(chatId)) //определяем состояние из кэша по id
                    .start(chatId, askUser, user, messageId, inlineMessageId);
        }//запускаем соответствующий сервис
        else {
            messages.add((T) new SendMessage(chatId, phrases.getMessage("phrase.NeedEnableService")));
        }
        return messages;
    }

    /**
     * Проверка наличия пользователя. Если не найден создается
     * Параметры из пакета update
     *
     * @param idUser    id
     * @param firstName имя
     * @param lastName  фамилия
     * @param userName  псевдоним
     * @return пользователя
     */
    private User checkUser(long idChat, int idUser, String firstName, String lastName, String userName) { //Создает юзера если такого нет
        if (dataAccessUser.findUser(idUser) == null) {
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUserName(userName);
            user.setIdUser(idUser);
            user.setState(null);
            user.setSubjectTask(new SubjectTaskUser[0]);
            user.setIdChat(idChat);
            user.setIdSaveBook(null);
            dataAccessUser.addUser(user);
            return user;
        }
        return dataAccessUser.findUser(idUser);
    }
}