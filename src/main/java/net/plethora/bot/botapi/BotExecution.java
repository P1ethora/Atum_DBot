package net.plethora.bot.botapi;

import net.plethora.bot.cache.CacheUsersState;
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

        if (update.hasCallbackQuery()) {                                           //если нажата кнопка
            CallbackQuery callbackQuery = update.getCallbackQuery();  //данные нажатия кнопки
            chatId = callbackQuery.getMessage().getChatId();          //id кнопки
            askUser = callbackQuery.getData().toLowerCase();          //содержимое кнопки(переводим в нижни реестр)
            if (cacheUsersState.getStateUsers().get(chatId) != null) {//Если активировано состояние
                sendMessage = processingStates.processing(cacheUsersState.getStateUsers().get(chatId)) //определяем состояние из кэша по id
                        .start(chatId, askUser);                                                       //запускаем соответствующий сервис
            } else return null;
        } else if (update.getMessage() != null && update.getMessage().hasText()) {  //если отправленно сообщение
            chatId = update.getMessage().getChatId();
            askUser = update.getMessage().getText().toLowerCase();

            //перечень возможных команд
            if (askUser.equals("/start")) {
                if (cacheUsersState.getStateUsers().get(chatId) != null) {
                    cacheUsersState.getStateUsers().remove(chatId); //переходим в неопределенное сотояние
                }
                return new SendMessage(chatId, "Неопределенное состояние, используй команды:\n" +
                        "/menu\n/ask\n/task\n/job\n/help");

            } else if (askUser.equals("/menu")) {
                return new SendMessage(chatId, menu.start(update));   //открываем меню

            } else if (askUser.equals("/ask")) {   //Состояние вопрос-ответ
                cacheUsersState.getStateUsers().put(chatId, BotState.ASK);
                return new SendMessage(chatId, "Спроси меня про java");


            } else if (cacheUsersState.getStateUsers().get(chatId) != null) {  //Если имеется состояние
                sendMessage = processingStates.processing(cacheUsersState.getStateUsers().get(chatId)) //определяем и запускаем сервис
                        .start(chatId, askUser);

            } else
                sendMessage = new SendMessage(chatId, "войдите в один из сервисов:\n/ask\n/task\n/job");  //если сообщение из неопр сост не команда

        }

        return sendMessage;
    }
}