package net.plethora.bot.botapi;

import net.plethora.bot.botapi.commands.Cmd;
import net.plethora.bot.cache.CacheUsersState;
import net.plethora.bot.dao.DataAccessUser;
import net.plethora.bot.model.User;
import net.plethora.bot.service.PhrasesService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

@Component
public class BotExecution<T> {

    private CacheUsersState cacheUsersState;
    private ProcessingStates processingStates;
    private KeyboardMenu keyboardMenu;
    private PhrasesService phrases;
    private DataAccessUser dataAccessUser;

    public BotExecution(CacheUsersState cacheUsersState, ProcessingStates processingStates, KeyboardMenu keyboardMenu, PhrasesService phrases, DataAccessUser dataAccessUser) {
        this.cacheUsersState = cacheUsersState;
        this.processingStates = processingStates;
        this.keyboardMenu = keyboardMenu;
        this.phrases = phrases;
        this.dataAccessUser = dataAccessUser;
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

        //если нажата кнопка
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();  //данные нажатия кнопки
            chatId = callbackQuery.getMessage().getChatId();          //id кнопки
            askUser = callbackQuery.getData().toLowerCase();          //содержимое кнопки(переводим в нижни реестр)

            if (checkCommand(chatId, askUser) != null) {     //является ли командой
                messages = (List<T>) checkCommand(chatId, askUser);  //соглассно введенной команды
            } else {                                         //Иначе запрос согласно сервиса меню
                //TODO дубль кода
                if (cacheUsersState.getStateUsers().get(chatId) != null) {//Если активировано состояние
                    String firstName = update.getCallbackQuery().getFrom().getFirstName();
                    String lastName = update.getCallbackQuery().getFrom().getLastName();
                    String userName = update.getCallbackQuery().getFrom().getUserName();
                    int idUser = update.getCallbackQuery().getFrom().getId();
                    messages = processingStates.processing(cacheUsersState.getStateUsers().get(chatId)) //определяем состояние из кэша по id
                            .start(chatId, askUser,checkUser(idUser,firstName,lastName,userName));                                                       //запускаем соответствующий сервис
                } else {
                    messages.add((T) new SendMessage(chatId, "Необходимо активировать состояние"));
                    return messages;
                }
                ;//на всякий случай
            }

            //если пришло соощение или нажата кнопка меню
        } else if (update.getMessage() != null && update.getMessage().hasText()) {  //если отправленно сообщение
            chatId = update.getMessage().getChatId();  //id чата
            askUser = update.getMessage().getText().toLowerCase();   //запрос пользователя

            String firstName = update.getMessage().getFrom().getFirstName();
            String lastName = update.getMessage().getFrom().getLastName();
            String userName = update.getMessage().getFrom().getUserName();
            int idUser = update.getMessage().getFrom().getId();

            if (checkCommand(chatId, askUser) != null) { //Сперва проверяются команды
                messages = (List<T>) checkCommand(chatId, askUser);   //запрос -> команда
            } else {                                                 //Если не является командой, запрос согласно сервиса меню
//TODO вынести в метод
                if (cacheUsersState.getStateUsers().get(chatId) != null) {  //Если имеется состояние
                    messages = processingStates.processing(cacheUsersState.getStateUsers().get(chatId)) //определяем и запускаем сервис
                            .start(chatId, askUser,checkUser(idUser,firstName,lastName,userName));

                } else {  //Если варианты не совпадают пользователь не подключил сервис
                    messages.add((T) new SendMessage(chatId, phrases.getMessage("phrase.NeedEnableService")));
                }

            }
        }

        return (List<T>) messages;
    }

    /**
     * Проверка на ввод команд
     *
     * @param chatId  id чата пользователя
     * @param askUser сообщение пользователя
     * @return готовое телеграм сообщение к отправке
     */
    private List<SendMessage> checkCommand(long chatId, String askUser) {
        switch (askUser) {
            case Cmd.START: {
                List<SendMessage> messages = new ArrayList<>();
                if (cacheUsersState.getStateUsers().get(chatId) != null) {
                    cacheUsersState.getStateUsers().remove(chatId); //переходим в неопределенное сотояние
                }
                messages.add(new SendMessage(chatId, phrases.getMessage("phrase.NeedEnableService")));
                return messages;

            }
            case Cmd.MENU: {
                List<SendMessage> messages = new ArrayList<>();
                messages.add(keyboardMenu.process(chatId));
                return messages;   //открываем меню

            }
            case Cmd.HELP:
            case Cmd.HELP_BUTTON: {
                List<SendMessage> messages = new ArrayList<>();
                messages.add(new SendMessage(chatId, phrases.getMessage("phrase.help")));
                return messages;

            }
            case Cmd.ASK:
            case Cmd.ASK_BUTTON: {   //Состояние вопрос-ответ
                List<SendMessage> messages = new ArrayList<>();
                cacheUsersState.getStateUsers().put(chatId, BotState.ASK);
                messages.add(new SendMessage(chatId, phrases.getMessage("phrase.AskEnableService")));
                return messages;

                //TODO подключить inline клаву к сообщению(перечень тем)
            }
            case Cmd.TASK:
            case Cmd.TASK_BUTTON: {   //Состояние задача
                List<SendMessage> messages = new ArrayList<>();
                cacheUsersState.getStateUsers().put(chatId, BotState.TASK);
                messages.add(new SendMessage(chatId, phrases.getMessage("phrase.TaskEnableService")));
                return messages;

            }
            case Cmd.JOB:
            case Cmd.JOB_BUTTON: {   //Состояние поиск работы
                List<SendMessage> messages = new ArrayList<>();
                cacheUsersState.getStateUsers().put(chatId, BotState.JOB);
                messages.add(new SendMessage(chatId, phrases.getMessage("phrase.JobEnableService")));
                return messages;
            }
        }

        return null;
    }

    private User checkUser(int idUser, String firstName, String lastName, String userName) { //Создает юзера если такого нет

        if(dataAccessUser.findUser(idUser)==null){
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUserName(userName);
            user.setIdUser(idUser);
            user.setReceivedTasks(new String[0]);
            dataAccessUser.addUser(user);
            return user;
        }
        return dataAccessUser.findUser(idUser);
    }
}