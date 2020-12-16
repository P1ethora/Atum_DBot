package net.plethora.bot.botapi.keyboards.kbjob;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardJobChoiceVacancy {

    public InlineKeyboardMarkup keyboard(int numberVacancy, int limit, String urlRespond, String urlDetailed, int idInfoSearch) {

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();  //ряды
        List<InlineKeyboardButton> rowTop = new ArrayList<>(); //ряд
        List<InlineKeyboardButton> rowDown = new ArrayList<>(); //ряд

        InlineKeyboardButton keyboardButtonReturn = new InlineKeyboardButton("Вернуться");
        keyboardButtonReturn.setCallbackData(":return>" + idInfoSearch);
        rowDown.add(keyboardButtonReturn);

        InlineKeyboardButton keyboardButtonRespond = new InlineKeyboardButton("Откликнуться"); //кнопка
        //keyboardButtonRespond.setCallbackData(":res!po<nd");
        keyboardButtonRespond.setUrl(urlRespond);
        rowDown.add(keyboardButtonRespond);

        InlineKeyboardButton keyboardButtonDetailed = new InlineKeyboardButton("Подробнее"); //кнопка
        //keyboardButtonDetailed.setCallbackData(":det!ai>led");
        keyboardButtonDetailed.setUrl(urlDetailed);
        rowDown.add(keyboardButtonDetailed);

        if (numberVacancy > 1) {
            InlineKeyboardButton keyboardButtonBack = new InlineKeyboardButton("Назад"); // кнопка
            keyboardButtonBack.setCallbackData("%b->ac!k");
            rowTop.add(keyboardButtonBack);
        }

        if (numberVacancy < limit) {
            InlineKeyboardButton keyboardButtonNext = new InlineKeyboardButton("Далее"); //кнопка
            keyboardButtonNext.setCallbackData("%n->ex!t");
            rowTop.add(keyboardButtonNext);
        }

        rows.add(rowTop);
        rows.add(rowDown);

        return new InlineKeyboardMarkup(rows);
    }
}