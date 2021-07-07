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

        InlineKeyboardButton buttonM = new InlineKeyboardButton("месяц");
        InlineKeyboardButton buttonW = new InlineKeyboardButton("неделя");
        InlineKeyboardButton buttonTd = new InlineKeyboardButton("три дня");
        InlineKeyboardButton buttonD = new InlineKeyboardButton("сутки");

        buttonM.setCallbackData("job#period#Месяц");
        buttonW.setCallbackData("job#periodНеделя");
        buttonTd.setCallbackData("job#period#Три дня");
        buttonD.setCallbackData("job#periodСутки");

        row.add(buttonM);
        row.add(buttonW);
        row.add(buttonTd);
        row.add(buttonD);
        rows.add(row);

        return new InlineKeyboardMarkup(rows);
    }
}