package net.plethora.bot.botapi.keyboards.kbjob;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardPeriodJob {

    public InlineKeyboardMarkup addKeyBoard() {

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

        return new InlineKeyboardMarkup(rows);
    }
}