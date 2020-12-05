package net.plethora.bot.botapi.keyboards;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;


@Component
public class KeyboardBookSelect {

    public KeyboardBookSelect() {

    }

    public InlineKeyboardMarkup keyboard(int numberBook, int limit, String url) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(); //клава

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();  //ряды
        List<InlineKeyboardButton> rowTop = new ArrayList<>(); //ряд
        List<InlineKeyboardButton> rowDown = new ArrayList<>(); //ряд

        InlineKeyboardButton keyboardButtonBackToOptions = new InlineKeyboardButton("К разделам"); //кнопка
        keyboardButtonBackToOptions.setCallbackData(":To<Opt!io>ns");
        rowDown.add(keyboardButtonBackToOptions);

        InlineKeyboardButton keyboardButton = new InlineKeyboardButton("Просмотр").setUrl(url); //кнопка
        rowDown.add(keyboardButton);

        if (numberBook > 1) {
            InlineKeyboardButton keyboardButtonBack = new InlineKeyboardButton("Назад"); // кнопка
            keyboardButtonBack.setCallbackData("%b->ac!k");
            rowTop.add(keyboardButtonBack);
        }

        InlineKeyboardButton keyboardButtonPreview = new InlineKeyboardButton("Превью"); // кнопка
        keyboardButtonPreview.setCallbackData(":previ!ew&");


        if (numberBook < limit) {
            InlineKeyboardButton keyboardButtonNext = new InlineKeyboardButton("Далее"); //кнопка
            keyboardButtonNext.setCallbackData("%n->ex!t");
            rowTop.add(keyboardButtonNext);
        }
        if (rowTop.size() == 2) {
            rowTop.add(1, keyboardButtonPreview);
        } else {
            if (rowTop.get(0).getText().equals("Назад")) {
                rowTop.add(1, keyboardButtonPreview);
            } else {
                rowTop.add(0, keyboardButtonPreview);
            }
        }
        rows.add(rowTop);
        rows.add(rowDown);

        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }
}