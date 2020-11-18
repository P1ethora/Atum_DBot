package net.plethora.bot.service;

import net.plethora.bot.botapi.parsers.ParsRabota;
import net.plethora.bot.dao.DataAccessArea;
import net.plethora.bot.model.Area;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobServiceMenu implements ServiceMenu {

    private ParsRabota parsRabota;
    private DataAccessArea dataAccessArea;

    public JobServiceMenu(ParsRabota parsRabota, DataAccessArea dataAccessArea) {
        this.parsRabota = parsRabota;
        this.dataAccessArea = dataAccessArea;
    }

    @Override
    public List<SendMessage> start(long chatId, String msgUser) {
        List<SendMessage> list = new ArrayList<>();
        //TODO если название составное этот IgnoreCase не сраотает(должно быть каждое слово с большой буквы)
        String areaName = msgUser.substring(0,1).toUpperCase()+msgUser.substring(1).toLowerCase(); //перевод case
        Area area = dataAccessArea.handleRequest(areaName); //достаем из базы модель по запросу
        if(area==null){
            //TODO забросить сообщения в ресурс
            list.add(new SendMessage(chatId,"Город " + areaName + " не найден"));
        } else {
            int code = dataAccessArea.handleRequest(areaName).getCode();
            list = parsRabota.vacancyListMsg(chatId, code);
            if (list.size() == 0) {
                list.add(new SendMessage(chatId, "В городе " + areaName + " вакансий java engineer не обнаружено "));
            }
        }
        return list;
    }
}
