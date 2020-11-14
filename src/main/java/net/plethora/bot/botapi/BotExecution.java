package net.plethora.bot.botapi;

import net.plethora.bot.cache.CacheUsersState;
import net.plethora.bot.service.Help;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class BotExecution {

    private CacheUsersState cacheUsersState;
    private ProcessingStates processingStates;
    private Menu menu;

    public BotExecution(CacheUsersState cacheUsersState, ProcessingStates processingStates, Menu menu) {
        this.cacheUsersState = cacheUsersState;
        this.processingStates = processingStates;
        this.menu = menu;
    }

    public SendMessage process(Update update) {

        long chatId;
        String askUser;
        SendMessage sendMessage = null;

        //если нажата inline кнопка
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();  //данные нажатия кнопки
            chatId = callbackQuery.getMessage().getChatId();          //id кнопки
            askUser = callbackQuery.getData().toLowerCase();          //содержимое кнопки(переводим в нижни реестр)

            if (checkCommand(update, chatId, askUser) != null) {
                sendMessage = checkCommand(update, chatId, askUser);
            } else {
                if (cacheUsersState.getStateUsers().get(chatId) != null) {//Если активировано состояние
                    sendMessage = processingStates.processing(cacheUsersState.getStateUsers().get(chatId)) //определяем состояние из кэша по id
                            .start(chatId, askUser);                                                       //запускаем соответствующий сервис
                } else return null;
            }

            //если пришло соощение или нажата кнопка меню
        } else if (update.getMessage() != null && update.getMessage().hasText()) {  //если отправленно сообщение
            chatId = update.getMessage().getChatId();
            askUser = update.getMessage().getText().toLowerCase();

            if (checkCommand(update, chatId, askUser) != null) { //Сперва проверяются команды
                sendMessage = checkCommand(update, chatId, askUser);
            } else {                                                 //Если не является командой проверяется состояние

                if (cacheUsersState.getStateUsers().get(chatId) != null) {  //Если имеется состояние
                    sendMessage = processingStates.processing(cacheUsersState.getStateUsers().get(chatId)) //определяем и запускаем сервис
                            .start(chatId, askUser);

                } else
                    sendMessage = new SendMessage(chatId, "войдите в один из сервисов:\n/ask\n/task\n/job\nили воспользуйтесь /help");  //если сообщение из неопр сост не команда

            }
        }

        return sendMessage;
    }

    private SendMessage checkCommand(Update update, long chatId, String askUser) {
        if (askUser.equals("/start")) {
            if (cacheUsersState.getStateUsers().get(chatId) != null) {
                cacheUsersState.getStateUsers().remove(chatId); //переходим в неопределенное сотояние
            }
            return new SendMessage(chatId, "Неопределенное состояние, используй команды:\n" +
                    "/menu\n/ask\n/task\n/job\n/help");

        } else if (askUser.equals("/menu")) {
            return menu.process(update);   //открываем меню

        } else if (askUser.equals("/help")||askUser.equals("help")) {
            return new SendMessage(chatId, Help.help);   //открываем меню

        } else if (askUser.equals("/ask") || askUser.equals("ask")) {   //Состояние вопрос-ответ
            cacheUsersState.getStateUsers().put(chatId, BotState.ASK);
            return new SendMessage(chatId, "Спроси меня про теорию java");

            //TODO подключить inline клаву к сообщению(перечень тем)
        } else if (askUser.equals("/task") || askUser.equals("task")) {   //Состояние задача
            cacheUsersState.getStateUsers().put(chatId, BotState.TASK);
            return new SendMessage(chatId, "На какую тему нужна задача?");

        } else if (askUser.equals("/job") || askUser.equals("job")) {   //Состояние поиск работы
            cacheUsersState.getStateUsers().put(chatId, BotState.JOB);
            return new SendMessage(chatId, "Ваша страна?");
        }

        return null;
    }
}