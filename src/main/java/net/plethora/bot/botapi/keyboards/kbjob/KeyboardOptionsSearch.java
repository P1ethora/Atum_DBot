package net.plethora.bot.botapi.keyboards.kbjob;

import net.plethora.bot.model.systemmodel.InfoForSearch;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardOptionsSearch {

    public InlineKeyboardMarkup keyboard(InfoForSearch infoForSearch) {

        List<List<InlineKeyboardButton>> rows = new ArrayList<>(); //формируем ряды
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();

        InlineKeyboardButton buttonC = new InlineKeyboardButton("Город");
        InlineKeyboardButton buttonP = new InlineKeyboardButton("Период");
        InlineKeyboardButton buttonS = new InlineKeyboardButton("Найти");
        String area;
        String period;
        String id;
if(infoForSearch==null){
    area = null;
    period = null;
    id = null;
} else{
    area = infoForSearch.getArea();
    period = infoForSearch.getPeriod();
    id = String.valueOf(infoForSearch.getId());
}

        buttonC.setCallbackData(":ar!-ea>" + area + "$" + period + "$" + id);
        buttonP.setCallbackData(":period>" + area+ "$" + period + "$" + id);
        buttonS.setCallbackData(":search>" +area + "$" + period + "$" + id);

        row.add(buttonC);
        row.add(buttonP);
        row1.add(buttonS);
        rows.add(row);
        rows.add(row1);

        return new InlineKeyboardMarkup(rows);
    }
}