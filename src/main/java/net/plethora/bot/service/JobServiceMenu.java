package net.plethora.bot.service;

import net.plethora.bot.botapi.parsers.ParsRabota;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class JobServiceMenu implements ServiceMenu {

    private ParsRabota parsRabota;

    public JobServiceMenu(ParsRabota parsRabota) {
        this.parsRabota = parsRabota;
    }

    @Override
    public SendMessage start(long chatId, String msgUser) {
        return parsRabota.vacancyListMsg(chatId,msgUser);
    }
}
