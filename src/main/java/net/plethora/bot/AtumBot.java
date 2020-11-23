package net.plethora.bot;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.plethora.bot.botapi.BotExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Getter
@Setter
public class AtumBot<T> extends TelegramWebhookBot {

    private String botPath;
    private String botUsername;
    private String botToken;

    @Autowired
    private BotExecution botExecution;

    public AtumBot(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    @SneakyThrows
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        send(botExecution.process(update));
        return null;
    }

    @SneakyThrows
    private void send(List<T> list) {
        for (T sendMessage : list) {
            if(sendMessage instanceof SendMessage)
            execute((SendMessage)sendMessage);
            if(sendMessage instanceof SendDocument)
                execute((SendDocument) sendMessage);
        }
        list.clear();
    }
}