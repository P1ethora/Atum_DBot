package net.plethora.bot;


import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.plethora.bot.botapi.BotState;
import net.plethora.bot.botapi.HandlerMessage;
import net.plethora.bot.botapi.ListCommand;
import net.plethora.bot.cache.CacheBotState;
import net.plethora.bot.model.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@Setter
public class AtumBot extends TelegramWebhookBot {

    private String botPath;
    private String botUsername;
    private String botToken;

    @Autowired
    private HandlerMessage handlerMsg;
    @Autowired
    private ListCommand listCommand;
    @Autowired
    private CacheBotState cacheBotState;

    private Answer answer;
    private final String NEGATIVE_ANSWER = "хм... Про это ничего не знаю";

    public AtumBot(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    @SneakyThrows
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        long chatId = update.getMessage().getChatId();
        String askUser = null;

        if (update.hasCallbackQuery()) {  //если нажата кнопка
            CallbackQuery callbackQuery = update.getCallbackQuery();
            chatId = callbackQuery.getMessage().getChatId();
            askUser = callbackQuery.getData().toLowerCase();
        } else if (update.getMessage() != null && update.getMessage().hasText()) { //если отправлено сообщение
            if (cacheBotState.getStateUsers().size()==0){
                execute(new SendMessage(chatId,  "Готов ответить на твой вопрос по теории java"));
                execute(new SendMessage(chatId,  "Что же ты хочешь узнать?"));
                cacheBotState.getStateUsers().put(chatId, BotState.START);
                return null;
            }
            askUser = update.getMessage().getText().toLowerCase();}


        execute(handlerMsg.sendMessage(chatId, askUser)); //отправка сообщения клиенту
        return null;
    }
}