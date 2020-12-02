package net.plethora.bot.botapi.keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardPeriodJob {

    public InlineKeyboardMarkup addKeyBoard() {

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
