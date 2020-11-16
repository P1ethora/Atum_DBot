package net.plethora.bot.botapi;

import net.plethora.bot.botapi.commands.Cmd;
import net.plethora.bot.cache.CacheUsersState;
import net.plethora.bot.service.PhrasesService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class BotExecution {

    private CacheUsersState cacheUsersState;
    private ProcessingStates processingStates;
    private KeyboardMenu keyboardMenu;

    private PhrasesService phrases;

    public BotExecution(CacheUsersState cacheUsersState, ProcessingStates processingStates, KeyboardMenu keyboardMenu,PhrasesService phrases) {
        this.cacheUsersState = cacheUsersState;
        this.processingStates = processingStates;
        this.keyboardMenu = keyboardMenu;
        this.phrases = phrases;
    }

    /**
     * Исполнение запроса: сообщение(hasText) или кнопка(hasCallbackQuery)
     *
     * @param update пакет от пользователя
     * @return телеграмм сообщение для пользователя
     */

    public SendMessage process(Update update) {

        long chatId;
        String askUser;
        SendMessage sendMessage = null;

        //если нажата inline кнопка
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();  //данные нажатия кнопки
            chatId = callbackQuery.getMessage().getChatId();          //id кнопки
            askUser = callbackQuery.getData().toLowerCase();          //содержимое кнопки(переводим в нижни реестр)

            if (checkCommand(chatId, askUser) != null) {     //является ли командой
                sendMessage = checkCommand(chatId, askUser);  //соглассно введенной команды
            } else {                                         //Иначе запрос согласно сервиса меню
                //TODO дубль кода
                if (cacheUsersState.getStateUsers().get(chatId) != null) {//Если активировано состояние
                    sendMessage = processingStates.processing(cacheUsersState.getStateUsers().get(chatId)) //определяем состояние из кэша по id
                            .start(chatId, askUser);                                                       //запускаем соответствующий сервис
                } else return null;//на всякий случай
            }

            //если пришло соощение или нажата кнопка меню
        } else if (update.getMessage() != null && update.getMessage().hasText()) {  //если отправленно сообщение
            chatId = update.getMessage().getChatId();  //id чата
            askUser = update.getMessage().getText().toLowerCase();   //запрос пользователя

            if (checkCommand(chatId, askUser) != null) { //Сперва проверяются команды
                sendMessage = checkCommand(chatId, askUser);   //запрос -> команда
            } else {                                                 //Если не является командой, запрос согласно сервиса меню
//TODO вынести в метод
                if (cacheUsersState.getStateUsers().get(chatId) != null) {  //Если имеется состояние
                    sendMessage = processingStates.processing(cacheUsersState.getStateUsers().get(chatId)) //определяем и запускаем сервис
                            .start(chatId, askUser);

                } else   //Если варианты не совпадают пользователь не подключил сервис
                    sendMessage = new SendMessage(chatId, phrases.getMessage("phrase.NeedEnableService"));

            }
        }

        return sendMessage;
    }

    /**
     * Проверка на ввод команд
     *
     * @param chatId  id чата пользователя
     * @param askUser сообщение пользователя
     * @return готовое телеграм сообщение к отправке
     */
    private SendMessage checkCommand(long chatId, String askUser) {
        if (askUser.equals(Cmd.START)) {
            if (cacheUsersState.getStateUsers().get(chatId) != null) {
                cacheUsersState.getStateUsers().remove(chatId); //переходим в неопределенное сотояние
            }
            return new SendMessage(chatId, phrases.getMessage("phrase.NeedEnableService"));

        } else if (askUser.equals(Cmd.MENU)) {
            return keyboardMenu.process(chatId);   //открываем меню

        } else if (askUser.equals(Cmd.HELP) || askUser.equals(Cmd.HELP_BUTTON)) {
            return new SendMessage(chatId, phrases.getMessage("phrase.help"));   //открываем меню

        } else if (askUser.equals(Cmd.ASK) || askUser.equals(Cmd.ASK_BUTTON)) {   //Состояние вопрос-ответ
            cacheUsersState.getStateUsers().put(chatId, BotState.ASK);
            return new SendMessage(chatId, phrases.getMessage("phrase.AskEnableService"));

            //TODO подключить inline клаву к сообщению(перечень тем)
        } else if (askUser.equals(Cmd.TASK) || askUser.equals(Cmd.TASK_BUTTON)) {   //Состояние задача
            cacheUsersState.getStateUsers().put(chatId, BotState.TASK);
            return new SendMessage(chatId, phrases.getMessage("phrase.TaskEnableService"));

        } else if (askUser.equals(Cmd.JOB) || askUser.equals(Cmd.JOB_BUTTON)) {   //Состояние поиск работы
            cacheUsersState.getStateUsers().put(chatId, BotState.JOB);
            return new SendMessage(chatId, phrases.getMessage("phrase.JobEnableService"));
        }

        return null;
    }
}