package net.plethora.bot.botapi;

import net.plethora.bot.botapi.commands.Cmd;
import net.plethora.bot.botapi.handler.handtask.SubjectTaskUser;
import net.plethora.bot.botapi.keyboards.KeyboardMenu;
import net.plethora.bot.botapi.keyboards.KeyboardSubjectTask;
import net.plethora.bot.botapi.state.BotState;
import net.plethora.bot.cache.CacheUsersState;
import net.plethora.bot.dao.DataAccessUser;
import net.plethora.bot.model.User;
import net.plethora.bot.service.PhrasesService;
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
    private KeyboardMenu keyboardMenu;
    private PhrasesService phrases;
    private DataAccessUser dataAccessUser;
    private KeyboardSubjectTask keyboardSubjectTask;

    private User user;

    public BotExecution(CacheUsersState cacheUsersState, ProcessingStates processingStates, KeyboardMenu keyboardMenu, PhrasesService phrases, DataAccessUser dataAccessUser, KeyboardSubjectTask keyboardSubjectTask) {
        this.cacheUsersState = cacheUsersState;
        this.processingStates = processingStates;
        this.keyboardMenu = keyboardMenu;
        this.phrases = phrases;
        this.dataAccessUser = dataAccessUser;
        this.keyboardSubjectTask = keyboardSubjectTask;
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
            chatId = callbackQuery.getMessage().getChatId();          //id кнопки
            askUser = callbackQuery.getData().toLowerCase();          //содержимое кнопки(переводим в нижни реестр)
            int messageId = callbackQuery.getMessage().getMessageId();

            if (checkCommand(chatId, askUser) != null) {     //является ли командой
                messages = (List<T>) checkCommand(chatId, askUser);  //соглассно введенной команды
            } else {                                         //Иначе запрос согласно сервиса меню
                //Определяем данные пользователя
                String firstName = update.getCallbackQuery().getFrom().getFirstName();
                String lastName = update.getCallbackQuery().getFrom().getLastName();
                String userName = update.getCallbackQuery().getFrom().getUserName();
                int idUser = update.getCallbackQuery().getFrom().getId();
                user = checkUser(chatId, idUser, firstName, lastName, userName);
                //включаем сервис
                messages = enabledService(chatId, askUser, messageId);
                return messages;
            }
//-----------------------------------------------------------------------------------------------------//
            //СООБЩЕНИЕ
        } else if (update.getMessage() != null && update.getMessage().hasText()) {  //если отправленно сообщение
            chatId = update.getMessage().getChatId();  //id чата
            askUser = update.getMessage().getText().toLowerCase();   //запрос пользователя

            String firstName = update.getMessage().getFrom().getFirstName();
            String lastName = update.getMessage().getFrom().getLastName();
            String userName = update.getMessage().getFrom().getUserName();
            int idUser = update.getMessage().getFrom().getId();
            user = checkUser(chatId, idUser, firstName, lastName, userName);
//TODO поменять это снизу
            if (checkCommand(chatId, askUser) != null) { //Сперва проверяются команды
                messages = (List<T>) checkCommand(chatId, askUser);   //запрос -> команда
            } else {                                                 //Если не является командой, запрос согласно сервиса меню

                messages = enabledService(chatId, askUser, 0);

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
    private List<SendMessage> checkCommand(long chatId, String askUser) {
        List<SendMessage> messages = new ArrayList<>();
        switch (askUser) {
            case Cmd.START: {
                if (cacheUsersState.getStateUsers().get(chatId) != null) {
                    cacheUsersState.getStateUsers().remove(chatId); //переходим в неопределенное сотояние
                }
                messages.add(new SendMessage(chatId, phrases.getMessage("phrase.NeedEnableService")));
                return messages;

            }
            case Cmd.MENU: {
                messages.add(keyboardMenu.process(chatId));
                return messages;   //открываем меню

            }
            case Cmd.HELP:
            case Cmd.HELP_BUTTON: {
                messages.add(new SendMessage(chatId, phrases.getMessage("phrase.help")));
                return messages;

            }
            case Cmd.ASK:
            case Cmd.ASK_BUTTON: {   //Состояние вопрос-ответ
                dataAccessUser.editUser(user, BotState.ASK);
                cacheUsersState.getStateUsers().put(chatId, BotState.ASK);
                messages.add(new SendMessage(chatId, phrases.getMessage("phrase.AskEnableService")));
                return messages;

                //TODO подключить inline клаву к сообщению(перечень тем)
            }
            case Cmd.TASK:
            case Cmd.TASK_BUTTON: {   //Состояние задача
                dataAccessUser.editUser(user, BotState.TASK);
                cacheUsersState.getStateUsers().put(chatId, BotState.TASK);
                messages.add(new SendMessage(chatId, phrases.getMessage("phrase.TaskEnableService")));
                messages.add(new SendMessage(chatId, "Разделы с задачами:")
                        .setReplyMarkup(keyboardSubjectTask.inlineKeyboardSubjectTask())); //кнопки
                return messages;

            }
            case Cmd.JOB:
            case Cmd.JOB_BUTTON: {   //Состояние поиск работы

                dataAccessUser.editUser(user, BotState.JOB);
                cacheUsersState.getStateUsers().put(chatId, BotState.JOB);
                messages.add(new SendMessage(chatId, phrases.getMessage("phrase.JobEnableService")));
                return messages;
            }
            case Cmd.BOOK:
                dataAccessUser.editUser(user, BotState.BOOK);
                cacheUsersState.getStateUsers().put(chatId, BotState.BOOK);
                messages.add(new SendMessage(chatId, "Сервис BOOK подключен"));
                messages.add(new SendMessage(chatId, "На каком языке предпочитаете книги?") );
                return messages;
        }

        return null;
    }

    /**
     * Включаем сервис или отправляем сообщение с запросом включить сервис
     * Параметры из пакета update
     *
     * @param chatId  id чата
     * @param askUser сообщение пользователя
     * @return список с сообщениями
     */
    private List<T> enabledService(long chatId, String askUser, int messageId) {
        List<T> messages = new ArrayList<>();
        if (cacheUsersState.getStateUsers().get(chatId) != null) {//Если активировано состояние

            messages = processingStates.processing(cacheUsersState.getStateUsers().get(chatId)) //определяем состояние из кэша по id
                    .start(chatId, askUser, user, messageId);
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