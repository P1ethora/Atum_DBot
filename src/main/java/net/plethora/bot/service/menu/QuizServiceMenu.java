package net.plethora.bot.service.menu;

import net.plethora.bot.botapi.handler.HandlerQuizMessage;
import net.plethora.bot.model.User;
import net.plethora.bot.service.ServiceMenu;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.List;

@Service
public class QuizServiceMenu implements ServiceMenu {

    private HandlerQuizMessage handlerQuizMessage;

    public QuizServiceMenu(HandlerQuizMessage handlerQuizMessage) {
        this.handlerQuizMessage = handlerQuizMessage;
    }

    @Override
    public List start(long chatId, String msgUser, User user, int messageId, String inlineMessageId) throws TelegramApiRequestException {


        return handlerQuizMessage.process(chatId);
    }
}
