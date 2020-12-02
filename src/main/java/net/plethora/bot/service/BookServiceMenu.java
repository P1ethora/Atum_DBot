package net.plethora.bot.service;

import net.plethora.bot.botapi.handler.handbook.HandlerBookMessage;
import net.plethora.bot.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookServiceMenu implements ServiceMenu {

    private HandlerBookMessage handlerBookMessage;

    public BookServiceMenu(HandlerBookMessage handlerBookMessage) {
        this.handlerBookMessage = handlerBookMessage;
    }

    @Override
    public List start(long chatId, String msgUser, User user, int messageId) {
        return handlerBookMessage.handler(chatId,msgUser,user,messageId);
    }
}
