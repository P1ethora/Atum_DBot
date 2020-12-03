package net.plethora.bot.service;

import net.plethora.bot.botapi.handler.HandlerAskMessage;
import net.plethora.bot.model.User;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;


@Service
public class AskServiceMenu<T> implements ServiceMenu {

    private HandlerAskMessage handlerAskMessage;

    public AskServiceMenu(HandlerAskMessage handlerAskMessage) {
        this.handlerAskMessage = handlerAskMessage;
    }

    @Override
    public List<T> start(long chatId, String askUser, User user, int messageId,String inlineMessageId){
        //TODO можно переделать handler на отправку списка а не SendMessage
        List<SendMessage> messages = new ArrayList<>();
        messages.add(handlerAskMessage.sendMessage(chatId,askUser));//запрос в базу
      return (List<T>) messages;
  }
}
