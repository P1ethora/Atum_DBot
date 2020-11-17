package net.plethora.bot.botapi;

import net.plethora.bot.botapi.commands.Cmd;
import net.plethora.bot.cache.CacheUsersState;
import net.plethora.bot.service.PhrasesService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

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

    public List<SendMessage> process(Update update) {

        long chatId;
        String askUser;
        //SendMessage sendMessage = null;
        List<SendMessage> messages = new ArrayList<>();
        //если нажата inline кнопка
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();  //данные нажатия кнопки
            chatId = callbackQuery.getMessage().getChatId();          //id кнопки
            askUser = callbackQuery.getData().toLowerCase();          //содержимое кнопки(переводим в нижни реестр)

            if (checkCommand(chatId, askUser) != null) {     //является ли командой
                messages = checkCommand(chatId, askUser);  //соглассно введенной команды
            } else {                                         //Иначе запрос согласно сервиса меню
                //TODO дубль кода
                if (cacheUsersState.getStateUsers().get(chatId) != null) {//Если активировано состояние
                    messages = processingStates.processing(cacheUsersState.getStateUsers().get(chatId)) //определяем состояние из кэша по id
                            .start(chatId, askUser);                                                       //запускаем соответствующий сервис
                } else return null;//на всякий случай
            }

            //если пришло соощение или нажата кнопка меню
        } else if (update.getMessage() != null && update.getMessage().hasText()) {  //если отправленно сообщение
            chatId = update.getMessage().getChatId();  //id чата
            askUser = update.getMessage().getText().toLowerCase();   //запрос пользователя

            if (checkCommand(chatId, askUser) != null) { //Сперва проверяются команды
                messages = checkCommand(chatId, askUser);   //запрос -> команда
            } else {                                                 //Если не является командой, запрос согласно сервиса меню
//TODO вынести в метод
                if (cacheUsersState.getStateUsers().get(chatId) != null) {  //Если имеется состояние
                    messages = processingStates.processing(cacheUsersState.getStateUsers().get(chatId)) //определяем и запускаем сервис
                            .start(chatId, askUser);

                } else {  //Если варианты не совпадают пользователь не подключил сервис
                    messages.add(new SendMessage(chatId, phrases.getMessage("phrase.NeedEnableService")));
                }

            }
        }

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
        if (askUser.equals(Cmd.START)) {
            List<SendMessage> messages = new ArrayList<>();
            if (cacheUsersState.getStateUsers().get(chatId) != null) {
                cacheUsersState.getStateUsers().remove(chatId); //переходим в неопределенное сотояние
            }
            messages.add(new SendMessage(chatId, phrases.getMessage("phrase.NeedEnableService")));
            return messages;

        } else if (askUser.equals(Cmd.MENU)) {
            List<SendMessage> messages = new ArrayList<>();
            messages.add(keyboardMenu.process(chatId));
            return messages;   //открываем меню

        } else if (askUser.equals(Cmd.HELP) || askUser.equals(Cmd.HELP_BUTTON)) {
            List<SendMessage> messages = new ArrayList<>();
            messages.add(new SendMessage(chatId, phrases.getMessage("phrase.help")));
            return messages;

        } else if (askUser.equals(Cmd.ASK) || askUser.equals(Cmd.ASK_BUTTON)) {   //Состояние вопрос-ответ
            List<SendMessage> messages = new ArrayList<>();
            cacheUsersState.getStateUsers().put(chatId, BotState.ASK);
            messages.add(new SendMessage(chatId, phrases.getMessage("phrase.AskEnableService")));
            return messages;

            //TODO подключить inline клаву к сообщению(перечень тем)
        } else if (askUser.equals(Cmd.TASK) || askUser.equals(Cmd.TASK_BUTTON)) {   //Состояние задача
            List<SendMessage> messages = new ArrayList<>();
            cacheUsersState.getStateUsers().put(chatId, BotState.TASK);
            messages.add(new SendMessage(chatId, phrases.getMessage("phrase.TaskEnableService")));
            return messages;

        } else if (askUser.equals(Cmd.JOB) || askUser.equals(Cmd.JOB_BUTTON)) {   //Состояние поиск работы
            List<SendMessage> messages = new ArrayList<>();
            cacheUsersState.getStateUsers().put(chatId, BotState.JOB);
            messages.add(new SendMessage(chatId, phrases.getMessage("phrase.JobEnableService")));
            return messages;
        }

        return null;
    }
}