package net.plethora.bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class JobServiceMenu implements ServiceMenu {
    @Override
    public SendMessage start(long chatId, String msgUser) {
        return null;
    }
}
