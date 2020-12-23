package net.plethora.bot.botapi.keyboards.kbjob;

import net.plethora.bot.model.Vacancy;
import net.plethora.bot.model.systemmodel.InfoForSearch;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardJobChoiceVacancy {

    public InlineKeyboardMarkup keyboard(int numberVacancy, int limit, Vacancy vacancy, InfoForSearch infoForSearch) {

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();  //ряды
        List<InlineKeyboardButton> rowTop = new ArrayList<>(); //ряд
        List<InlineKeyboardButton> rowDown = new ArrayList<>(); //ряд

        InlineKeyboardButton keyboardButtonReturn = new InlineKeyboardButton("Вернуться");
        keyboardButtonReturn.setCallbackData("job#found#:return>" + infoForSearch.getArea() + "$" + infoForSearch.getPeriod() + "$" + infoForSearch.getId());
        rowDown.add(keyboardButtonReturn);

        InlineKeyboardButton keyboardButtonRespond = new InlineKeyboardButton("Откликнуться"); //кнопка
        keyboardButtonRespond.setUrl(vacancy.getUrlRespond());
        rowDown.add(keyboardButtonRespond);

        InlineKeyboardButton keyboardButtonDetailed = new InlineKeyboardButton("Подробнее"); //кнопка
        keyboardButtonDetailed.setUrl(vacancy.getUrlVacancy());
        rowDown.add(keyboardButtonDetailed);

        if (numberVacancy > 1) {
            InlineKeyboardButton keyboardButtonBack = new InlineKeyboardButton("Назад"); // кнопка
            keyboardButtonBack.setCallbackData("job#found#:<--back" + infoForSearch.getArea() + "$" + infoForSearch.getPeriod() + "$" + vacancy.getId());
            rowTop.add(keyboardButtonBack);
        }

        if (numberVacancy < limit) {
            InlineKeyboardButton keyboardButtonNext = new InlineKeyboardButton("Далее"); //кнопка
            keyboardButtonNext.setCallbackData("job#found#:next-->" + infoForSearch.getArea() + "$" + infoForSearch.getPeriod() + "$" + vacancy.getId());
            rowTop.add(keyboardButtonNext);
        }

        rows.add(rowTop);
        rows.add(rowDown);

        return new InlineKeyboardMarkup(rows);
    }
}