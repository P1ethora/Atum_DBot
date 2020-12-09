package net.plethora.bot.botapi.keyboards.kbbook;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardAgeOptionBook {

    public InlineKeyboardMarkup inlineKeyboardSubjectTask() {

        List<List<InlineKeyboardButton>> rows = new ArrayList<>(); //список рядов
        List<InlineKeyboardButton> row = new ArrayList<>();//ряд

        InlineKeyboardButton buttonC = new InlineKeyboardButton("Детям");
        InlineKeyboardButton buttonO = new InlineKeyboardButton("Взрослым");

        buttonC.setCallbackData("children");
        buttonO.setCallbackData("older");

        row.add(buttonC);
        row.add(buttonO);

        rows.add(row);

        return new InlineKeyboardMarkup(rows);
    }
}