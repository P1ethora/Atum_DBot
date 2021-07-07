package net.plethora.bot.botapi;

import net.plethora.bot.botapi.commands.CheckCommand;
import net.plethora.bot.botapi.state.BotState;
import net.plethora.bot.botapi.state.SubState;
import net.plethora.bot.dao.DataAccessUser;
import net.plethora.bot.model.UserTelegram;
import net.plethora.bot.service.PhrasesService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollOption;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class BotExecution<T> {

    private ProcessingStates processingStates;
    private PhrasesService phrases;
    private DataAccessUser dataAccessUser;
    private UserTelegram userTelegram;
    private CheckCommand checkCommand;

    public BotExecution(ProcessingStates processingStates, PhrasesService phrases,
                        DataAccessUser dataAccessUser, CheckCommand checkCommand) {
        this.processingStates = processingStates;
        this.phrases = phrases;
        this.dataAccessUser = dataAccessUser;
        this.checkCommand = checkCommand;
    }

    /**
     * Исполнение запроса: сообщение(hasText) или кнопка(hasCallbackQuery)
     * ВСЕ ЗАПРОСЫ ПЕРЕВОДЯТСЯ В НИЖНИЙ РЕЕСТР
     *
     * @param update пакет от пользователя
     * @return сообщение для пользователя
     */
    public List<T> process(Update update) throws TelegramApiRequestException {

        long chatId;
        String msgUser;
        List<T> messages = new ArrayList<>();
//------------------------------------------------------------------------------------------------//
        //КНОПКА

        if (update.hasCallbackQuery()) {

            CallbackQuery callbackQuery = update.getCallbackQuery();  //данные нажатия кнопки
            int messageId = callbackQuery.getMessage().getMessageId();// id сообщения
            String callbackQueryId = callbackQuery.getId();           //id callback для всплывающего окна
            chatId = callbackQuery.getMessage().getChatId();                      //<---------------
            msgUser = callbackQuery.getData().toLowerCase();                      //               |
            String firstName = update.getCallbackQuery().getFrom().getFirstName();//    Данные     |
            String lastName = update.getCallbackQuery().getFrom().getLastName();  // Пользователя  |
            String userName = update.getCallbackQuery().getFrom().getUserName();  //               |
            int idUser = update.getCallbackQuery().getFrom().getId();             //<---------------

            messages = response(chatId, idUser, firstName, lastName, userName, changeState(msgUser), messageId, callbackQueryId);
//-----------------------------------------------------------------------------------------------------//
            //СООБЩЕНИЕ
        } else if (update.getMessage() != null && update.getMessage().hasText()) {  //если отправленно сообщение

            chatId = update.getMessage().getChatId();                       //<---------------
            msgUser = update.getMessage().getText().toLowerCase();          //                |
            String firstName = update.getMessage().getFrom().getFirstName();//     Данные     |
            String lastName = update.getMessage().getFrom().getLastName();  //   Пользователя |
            String userName = update.getMessage().getFrom().getUserName();  //                |
            int idUser = update.getMessage().getFrom().getId();             //<---------------

            userTelegram = getUser(chatId, idUser, firstName, lastName, userName); //инициализация пользователя

            messages = response(chatId, idUser, firstName, lastName, userName, msgUser, 0, null);

//---------------------------------------------------------------------------------------------------------//
            //ОТВЕТ ВИКТОРИНЫ
        } else if (update.hasPoll()) {
            Poll poll = update.getPoll();
//TODO можно собрать данные для вывода статистики вконце викторины
            String question = poll.getQuestion();
            String pollId = poll.getId(); //id Опроса
            int totalVoter = poll.getTotalVoterCount(); //количество ответов
            List<PollOption> pollOptions = poll.getOptions();  //варианты ответов
            System.out.println("Количество " + totalVoter + "Вариант " + pollOptions);
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
    private List<T> enabledService(long chatId, String askUser, int messageId, String inlineMessageId) throws TelegramApiRequestException {
        List<T> messages = new ArrayList<>();
        if (userTelegram.getState() != null) {//Если есть состояние
            //Определяем что за состояние и запускаем сервис
            messages = processingStates.processing(userTelegram.getState())
                    .start(chatId, askUser, userTelegram, messageId, inlineMessageId);
        } else { //Если не активирован сервис
            messages.add((T) new SendMessage(chatId, phrases.getMessage("phrase.NeedEnableService")));
        }

        return messages;
    }

    /**
     * Получаем пользователя
     *
     * @param idChat    id чата
     * @param idUser    id польователя
     * @param firstName Имя
     * @param lastName  Фамилия
     * @param userName  Псевдоним
     * @return Пользователя
     */
    private UserTelegram getUser(long idChat, int idUser, String firstName, String lastName, String userName) { //Создает юзера если такого нет
        UserTelegram userTelegram = dataAccessUser.findUser(idUser);
        if (userTelegram == null) {
            userTelegram = newUser(idChat, idUser, firstName, lastName, userName);
        }
        return userTelegram;
    }

    /**
     * Создаем нового пользователя
     *
     * @param idChat    id чата
     * @param idUser    id пользователя
     * @param firstName Имя
     * @param lastName  Фамилия
     * @param userName  Псевдоним
     * @return Пользователя
     */
    private UserTelegram newUser(long idChat, int idUser, String firstName, String lastName, String userName) {
        UserTelegram userTelegram = new UserTelegram();
        userTelegram.setFirstName(firstName);
        userTelegram.setLastName(lastName);
        userTelegram.setUserName(userName);
        userTelegram.setIdUser(idUser);
        userTelegram.setState(null);
        userTelegram.setSubState(null);
        userTelegram.setIdChat(idChat);
        userTelegram.setDate(new Date());
        dataAccessUser.addUser(userTelegram);
        return userTelegram;
    }

    /**
     * @param chatId          id ата
     * @param idUser          id пользователя
     * @param firstName       Имя
     * @param lastName        Фамилия
     * @param userName        Псевдоним
     * @param msgUser         сообщение от пользователя
     * @param messageId       id сообщения
     * @param callbackQueryId id вызова
     * @return лист-ответ с сообщением
     */
    private List<T> response(long chatId, int idUser, String firstName, String lastName, String userName, String msgUser, int messageId, String callbackQueryId) throws TelegramApiRequestException {
        List<T> messages;
        messages = checkCommand.inspect(chatId, msgUser, userTelegram, dataAccessUser, phrases); //получаем сообщение от бота согласно команде
        if (messages.size() == 0) {     //если не пришло сообщений значит не команда
            userTelegram = getUser(chatId, idUser, firstName, lastName, userName);//Инициализаци пользователя
            messages = enabledService(chatId, msgUser, messageId, callbackQueryId);  //включаем сервис
        }
        return messages;
    }

    private String changeState(String msgUser) {

        String[] sts = msgUser.split("#"); //делим сообщение на части
        BotState botState = BotState.valueOf(sts[0].toUpperCase()); //1я часть - состояние
        dataAccessUser.editUser(userTelegram, botState); //меняем состояние
        if (!sts[1].equals("")) { //на случай если подсостояние не указано
            SubState subState = SubState.valueOf(sts[1].toUpperCase());//2я - подсостояние
            dataAccessUser.editUser(userTelegram, subState); // меняем состояние
        }
        return sts[2];  // остальное возвращаем
    }
}