package net.plethora.atumBot;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Getter
@Setter
public class AtumBot extends TelegramWebhookBot {

    private String botPath;
    private String botUsername;
    private String botToken;

    public AtumBot (DefaultBotOptions botOptions){
        super(botOptions);
    }

    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if(update.getMessage() != null && update.getMessage().hasText()){
            long chatId = update.getMessage().getChatId();

            try{
                execute(new SendMessage(chatId, "You enter " + update.getMessage().getText()));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}