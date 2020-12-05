package net.plethora.bot.service.menu;

import net.plethora.bot.botapi.handler.HandlerBookMessage;
import net.plethora.bot.model.User;
import net.plethora.bot.service.ServiceMenu;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookServiceMenu implements ServiceMenu {

    private HandlerBookMessage handlerBookMessage;

    public BookServiceMenu(HandlerBookMessage handlerBookMessage) {
        this.handlerBookMessage = handlerBookMessage;
    }

    @Override
    public List start(long chatId, String msgUser, User user, int messageId,String inlineMessageId) {
        return handlerBookMessage.handler(chatId,msgUser,messageId, inlineMessageId);
    }
}
