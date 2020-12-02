package net.plethora.bot.botapi.keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardBookSelect {

    private int numberBook;
    private int limit;
    private String url;

    public KeyboardBookSelect(int numberBook, int limit, String url) {
        this.numberBook = numberBook;
        this.limit = limit;
        this.url = url;
    }

    public InlineKeyboardMarkup keyboard() {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(); //клава
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();  //ряды
        List<InlineKeyboardButton> rowTop = new ArrayList<>(); //ряд
        List<InlineKeyboardButton> rowDown = new ArrayList<>(); //ряд


        InlineKeyboardButton keyboardButton = new InlineKeyboardButton("Скачать").setUrl(url); //кнопка
        rowDown.add(keyboardButton);

        if (numberBook != 1) {
            InlineKeyboardButton keyboardButtonBack = new InlineKeyboardButton("Назад"); // кнопка
            keyboardButtonBack.setCallbackData("%b->ac!k");
            rowTop.add(keyboardButtonBack);
        }

        if (numberBook != limit) {
            InlineKeyboardButton keyboardButtonNext = new InlineKeyboardButton("Далее"); //кнопка
            keyboardButtonNext.setCallbackData("%n->ex!t");
            rowTop.add(keyboardButtonNext);
        }

        rows.add(rowTop);
        rows.add(rowDown);

        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

}
