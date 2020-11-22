package net.plethora.bot.botapi.handler;

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

    public HandlerJobMessage(CacheSearchJob cacheSearchJob, DataAccessArea dataAccessArea,ParsRabota parsRabota){
        this.cacheSearchJob = cacheSearchJob;
        this.dataAccessArea = dataAccessArea;
        this.parsRabota = parsRabota;
    }

public List<SendMessage> handler(long chatId, String msgUser){
    List<SendMessage> list = new ArrayList<>();
    //TODO: вынести в handlers
    InfoForSearch info = cacheSearchJob.getInfos().get(chatId);

        if (info == null) { //если нет в списке
        cacheSearchJob.getInfos().put(chatId, new InfoForSearch());//создаем
        cacheSearchJob.getInfos().get(chatId).setArea(msgUser);//добавляем
        //TODO:добавить текст в ресурсы
        //TODO проверка на период или оставить с неделей по умолчанию
        list.add(new SendMessage(chatId, "Выберите период:").setReplyMarkup(addKeyBoard()));
        return list;
    }  if (info.getPeriod() == null && info.getArea() != null) { //если нет периода
        info.setPeriod(msgUser); //добавляем
    } if (info.getArea() != null && info.getPeriod() != null) {

        //TODO если название составное этот IgnoreCase не сраотает(должно быть каждое слово с большой буквы)
        //TODO проще переписать базу везде ловерCase
        //String areaName = info.getArea().substring(0, 1).toUpperCase() + info.getArea().substring(1).toLowerCase(); //перевод case
        String areaName = info.getArea(); //перевод case
        Area areaObj = dataAccessArea.handleRequest(areaName); //достаем из базы модель по запросу
        if (areaObj == null) {
            //TODO забросить сообщения в ресурс
            //TODO проверку на город выше
            list.add(new SendMessage(chatId, "Город " + areaName + " не найден"));
        } else {
            int code = dataAccessArea.handleRequest(areaName).getCode();
            list = parsRabota.vacancyListMsg(chatId, code, info.getPeriod());
            if (list.size() == 0) {
                list.add(new SendMessage(chatId, "В городе " + areaName + " вакансий java engineer не обнаружено "));
            }
        }
        cacheSearchJob.getInfos().remove(chatId);
    }
        return list;
}

    private InlineKeyboardMarkup addKeyBoard(){

        InlineKeyboardMarkup keyBoardMsg = new InlineKeyboardMarkup();   //наша клава под msg
        List<List<InlineKeyboardButton>> rows = new ArrayList<>(); //формируем ряды
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton buttonM = new InlineKeyboardButton("Месяц");
        InlineKeyboardButton buttonW = new InlineKeyboardButton("Неделя");
        InlineKeyboardButton buttonTd = new InlineKeyboardButton("Три дня");
        InlineKeyboardButton buttonD = new InlineKeyboardButton("Сутки");

        buttonM.setCallbackData("Месяц");
        buttonW.setCallbackData("Неделя");
        buttonTd.setCallbackData("Три дня");
        buttonD.setCallbackData("Сутки");

        row.add(buttonM);
        row.add(buttonW);
        row.add(buttonTd);
        row.add(buttonD);
        rows.add(row);
        keyBoardMsg.setKeyboard(rows);
        return keyBoardMsg;
    }

}