package net.plethora.bot;


import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.dao.HandlerMsg;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Getter
@Setter
public class AtumBot extends TelegramWebhookBot {

    private String botPath;
    private String botUsername;
    private String botToken;
    private HandlerMsg dialog;

    public AtumBot(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        //TODO вынести логику в отдельный класс
        try {
            if (update.hasCallbackQuery()) {
                execute(dialog.sendMessage(update.getCallbackQuery().getMessage().getChatId(),
                        (update.getCallbackQuery().getData())).enableMarkdown(true));

            } else if (update.getMessage() != null && update.getMessage().hasText()) {

                long chatId = update.getMessage().getChatId();
                String askUser = update.getMessage().getText().toLowerCase();

                execute(dialog.sendMessage(chatId, askUser).enableMarkdown(true));
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}