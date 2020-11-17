package net.plethora.bot.service;

import net.plethora.bot.botapi.handler.HandlerAskMessage;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;


@Service
public class AskServiceMenu implements ServiceMenu {

    private HandlerAskMessage handlerAskMessage;

    public AskServiceMenu(HandlerAskMessage handlerAskMessage) {
        this.handlerAskMessage = handlerAskMessage;
    }

    @Override
    public List<SendMessage> start(long chatId, String askUser){
        List<SendMessage> messages = new ArrayList<>();
        messages.add(handlerAskMessage.sendMessage(chatId,askUser));//запрос в базу
      return messages;
  }
}
