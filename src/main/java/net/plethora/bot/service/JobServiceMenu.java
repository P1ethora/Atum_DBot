package net.plethora.bot.service;

import net.plethora.bot.botapi.parsers.ParsRabota;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Service
public class JobServiceMenu implements ServiceMenu {

    private ParsRabota parsRabota;

    public JobServiceMenu(ParsRabota parsRabota) {
        this.parsRabota = parsRabota;
    }

    @Override
    public List<SendMessage> start(long chatId, String msgUser) {
//List<SendMessage> messages = parsRabota.vacancyListMsg(chatId,msgUser);
        return parsRabota.vacancyListMsg(chatId,msgUser);
    }
}
