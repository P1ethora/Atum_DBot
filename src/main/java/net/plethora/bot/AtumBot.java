package net.plethora.bot;


import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.plethora.bot.botapi.HandlerMessage;
import net.plethora.bot.model.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
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

    private Answer answer;
    private final String NEGATIVE_ANSWER = "хм... Про это ничего не знаю";

    public AtumBot(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    @SneakyThrows
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        long chatId = 0;
        String askUser = null;

        if (update.hasCallbackQuery()) {  //если нажата кнопка
            CallbackQuery callbackQuery = update.getCallbackQuery();
            chatId = callbackQuery.getMessage().getChatId();
            askUser = callbackQuery.getData().toLowerCase();
        } else if (update.getMessage() != null && update.getMessage().hasText()) { //если отправлено сообщение
            chatId = update.getMessage().getChatId();
            askUser = update.getMessage().getText().toLowerCase();
        }

        execute(handlerMsg.sendMessage(chatId, askUser)); //отправка сообщения клиенту
        return null;
    }
}