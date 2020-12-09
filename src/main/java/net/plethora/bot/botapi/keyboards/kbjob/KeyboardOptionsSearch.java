package net.plethora.bot.botapi.keyboards.kbjob;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardOptionsSearch {

    public InlineKeyboardMarkup keyboard() {

        List<List<InlineKeyboardButton>> rows = new ArrayList<>(); //формируем ряды
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();

        InlineKeyboardButton buttonC = new InlineKeyboardButton("Город");
        InlineKeyboardButton buttonP = new InlineKeyboardButton("Период");
        InlineKeyboardButton buttonS = new InlineKeyboardButton("Найти");

        buttonC.setCallbackData(":a<r!ea");
        buttonP.setCallbackData(":per!io<d");
        buttonS.setCallbackData(":se|arc!h");

        row.add(buttonC);
        row.add(buttonP);
        row1.add(buttonS);
        rows.add(row);
        rows.add(row1);

        return new InlineKeyboardMarkup(rows);
    }
}