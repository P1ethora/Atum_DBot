package net.plethora.bot.service;

import net.plethora.bot.botapi.handler.handtask.HandlerTaskMessage;
import net.plethora.bot.model.User;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Service
public class TaskServiceMenu<T> implements ServiceMenu {

    private HandlerTaskMessage handlerTaskMessage;

    public TaskServiceMenu(HandlerTaskMessage handlerTaskMessage){
        this.handlerTaskMessage = handlerTaskMessage;
    }

    @Override
    public List<T> start(long chatId, String msgUser, User user, int messageId) {
        List<T> list = handlerTaskMessage.go(chatId,msgUser,user, messageId);

        if(list.size()==0){
            list.add((T) new SendMessage(chatId,"Не Найдено"));
        }

        return list;
    }
}
