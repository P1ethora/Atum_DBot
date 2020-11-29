package net.plethora.bot.service;

import net.plethora.bot.botapi.handler.handjob.HandlerJobMessage;
import net.plethora.bot.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceMenu<T> implements ServiceMenu {

    private final HandlerJobMessage handlerJobMessage;

    public JobServiceMenu(HandlerJobMessage handlerJobMessage) {
        this.handlerJobMessage = handlerJobMessage;
    }


    @Override
    public List<T> start(long chatId, String msgUser, User user, int messageId) {
        return (List<T>) handlerJobMessage.handler(chatId, msgUser);

    }

}
