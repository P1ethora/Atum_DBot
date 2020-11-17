package net.plethora.bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Service
public class TaskServiceMenu implements ServiceMenu {
    @Override
    public List<SendMessage> start(long chatId, String msgUser) {
        return null;
    }
}
