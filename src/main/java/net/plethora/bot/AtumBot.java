package net.plethora.bot;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.plethora.bot.botapi.BotExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
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
        Runnable run = () -> {
            try {
                send(botExecution.process(update));
            } catch (TelegramApiRequestException e) {
                e.printStackTrace();
            }
        };
        Thread executor = new Thread(run, "Executor");
        executor.start();

        return null;
    }

    @SneakyThrows
    private void send(List<T> list) {
        for (T sendMessage : list) {
            if (sendMessage instanceof SendMessage) //если является SendMessage
                execute((SendMessage) sendMessage);
            if (sendMessage instanceof SendDocument) //если SendDocument
                execute((SendDocument) sendMessage);
            if (sendMessage instanceof EditMessageText)
                execute((EditMessageText) sendMessage);
            if (sendMessage instanceof SendPhoto)
                execute((SendPhoto) sendMessage);
            if (sendMessage instanceof EditMessageMedia)
                execute((EditMessageMedia) sendMessage);
            if (sendMessage instanceof AnswerCallbackQuery)
                execute((AnswerCallbackQuery) sendMessage);
            if (sendMessage instanceof SendPoll)
                execute((SendPoll) sendMessage);
        }

        list.clear();
    }
}