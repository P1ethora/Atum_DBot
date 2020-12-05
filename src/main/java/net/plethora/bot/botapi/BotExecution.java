package net.plethora.bot.botapi;

import net.plethora.bot.botapi.commands.CheckCommand;
import net.plethora.bot.botapi.commands.Cmd;
import net.plethora.bot.botapi.keyboards.KeyboardCmdMenu;
import net.plethora.bot.botapi.state.BotState;
import net.plethora.bot.dao.DataAccessUser;
import net.plethora.bot.model.User;
import net.plethora.bot.service.PhrasesService;
import net.plethora.bot.botapi.system.systemMessage.AgeOptionBookMessage;
import net.plethora.bot.botapi.system.systemMessage.OptionTypeTaskMessage;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
public class BotExecution<T> {

    private ProcessingStates processingStates;
    private KeyboardCmdMenu keyboardCmdMenu;
    private PhrasesService phrases;
    private DataAccessUser dataAccessUser;
    private OptionTypeTaskMessage optionTypeTaskMessage;
    private AgeOptionBookMessage ageOptionBookMessage;
    private User user;
    private CheckCommand checkCommand;

    public BotExecution(ProcessingStates processingStates,
                        KeyboardCmdMenu keyboardCmdMenu, PhrasesService phrases,
                        DataAccessUser dataAccessUser, AgeOptionBookMessage ageOptionBookMessage,
                        OptionTypeTaskMessage optionTypeTaskMessage,CheckCommand checkCommand) {
        this.processingStates = processingStates;
        this.keyboardCmdMenu = keyboardCmdMenu;
        this.phrases = phrases;
        this.dataAccessUser = dataAccessUser;
        this.ageOptionBookMessage = ageOptionBookMessage;
        this.optionTypeTaskMessage = optionTypeTaskMessage;
        this.checkCommand = checkCommand;
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

            messages = checkCommand.inspect(chatId,askUser,user,dataAccessUser,phrases); //получаем сообщение от бота согласно команде
            if (messages.size() == 0) {     //если не пришло сообщений значит не команда

                String firstName = update.getCallbackQuery().getFrom().getFirstName();//<---------------
                String lastName = update.getCallbackQuery().getFrom().getLastName();  //   Данные      |
                String userName = update.getCallbackQuery().getFrom().getUserName();  //  Пользователя |
                int idUser = update.getCallbackQuery().getFrom().getId();             //<---------------

                user = getUser(chatId, idUser, firstName, lastName, userName);
                //включаем сервис
                messages = enabledService(chatId, askUser, messageId, callbackQueryId);
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

            user = getUser(chatId, idUser, firstName, lastName, userName);

            messages = checkCommand.inspect(chatId,askUser,user,dataAccessUser,phrases);   //проверка на команды,
            if (messages.size() == 0) {                      //Если не является командой, запрос согласно сервиса меню
                messages = enabledService(chatId, askUser, 0, null);
            }
        }
//---------------------------------------------------------------------------------------------------------//
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
        if (user.getState() != null) {//Если есть состояние
            messages = processingStates.processing(user.getState()).start(chatId, askUser, user, messageId, inlineMessageId);
        } else { //Если не активирован сервис
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
    private User getUser(long idChat, int idUser, String firstName, String lastName, String userName) { //Создает юзера если такого нет
        User user = dataAccessUser.findUser(idUser);
        if (user == null) {
            user = newUser(idChat, idUser, firstName, lastName, userName);
        }
        return user;
    }

    private User newUser(long idChat, int idUser, String firstName, String lastName, String userName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserName(userName);
        user.setIdUser(idUser);
        user.setState(null);
        user.setIdChat(idChat);
        dataAccessUser.addUser(user);
        return user;
    }
}