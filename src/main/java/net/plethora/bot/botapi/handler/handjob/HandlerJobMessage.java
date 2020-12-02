package net.plethora.bot.botapi.handler.handjob;

import net.plethora.bot.botapi.keyboards.KeyboardPeriodJob;
import net.plethora.bot.botapi.parsers.InfoForSearch;
import net.plethora.bot.botapi.parsers.ParsRabota;
import net.plethora.bot.cache.CacheSearchJob;
import net.plethora.bot.dao.DataAccessArea;
import net.plethora.bot.model.Area;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class HandlerJobMessage {

    private CacheSearchJob cacheSearchJob;
    private DataAccessArea dataAccessArea;
    private ParsRabota parsRabota;

    public HandlerJobMessage(CacheSearchJob cacheSearchJob, DataAccessArea dataAccessArea, ParsRabota parsRabota) {
        this.cacheSearchJob = cacheSearchJob;
        this.dataAccessArea = dataAccessArea;
        this.parsRabota = parsRabota;
    }

    public List<SendMessage> handler(long chatId, String msgUser) {
        List<SendMessage> list = new ArrayList<>();

        InfoForSearch info = cacheSearchJob.getInfos().get(chatId); //Информация для поиска

        if (info == null) { //если нет в списке
            cacheSearchJob.getInfos().put(chatId, new InfoForSearch());//создаем пустое инфо в кеше

            if (dataAccessArea.handleRequest(msgUser) == null){ //если город не найден
                list.add(new SendMessage(chatId, "Город " + msgUser + " не найден"));
                return list;
            }

            cacheSearchJob.getInfos().get(chatId).setArea(msgUser);//добавляем город в инфо кэш
            //TODO:добавить текст в ресурсы
            //TODO проверка на период или оставить с неделей по умолчанию
            list.add(new SendMessage(chatId, "Выберите период:").setReplyMarkup(new KeyboardPeriodJob().addKeyBoard()));
            return list;
        }
        if (info.getPeriod() == null && info.getArea() != null) { //если в инфо нет периода
            info.setPeriod(msgUser); //добавляем
        }
        if (info.getArea() != null && info.getPeriod() != null) {

            String areaName = info.getArea(); //получаем название нужного города

//                //TODO забросить сообщения в ресурс
                int code = dataAccessArea.handleRequest(areaName).getCode();
                list = parsRabota.vacancyListMsg(chatId, code, info.getPeriod());
                if (list.size() == 0) {
                    list.add(new SendMessage(chatId, "В городе " + areaName + " вакансий java engineer не обнаружено "));
                }
            cacheSearchJob.getInfos().remove(chatId); //Пока просто удаляем инфо в конце
            //TODO можно оставить инфо и дать пользователю возможность менять период или город
        }
        return list;
    }
}