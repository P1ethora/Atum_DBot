package net.plethora.bot.service;

import net.plethora.bot.botapi.handler.HandlerAskMessage;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


@Service
public class AskServiceMenu implements ServiceMenu {

    private HandlerAskMessage handlerAskMessage;

    public AskServiceMenu(HandlerAskMessage handlerAskMessage) {
        this.handlerAskMessage = handlerAskMessage;
    }

    @Override
    public SendMessage start(long chatId, String askUser){
      return handlerAskMessage.sendMessage(chatId,askUser); //запрос в базу
  }
}
