package net.plethora.bot.botapi.keyboards.kbquiz;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardSoloButtonQuiz {

    public InlineKeyboardMarkup keyboardStart() {

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();  //ряды
        List<InlineKeyboardButton> row = new ArrayList<>(); //ряд

        InlineKeyboardButton keyboardButton = new InlineKeyboardButton("Начать"); //имя кнопки
        keyboardButton.setCallbackData("quiz##:sta!r>t");  //начать
        row.add(keyboardButton); //добавляем кнопку

        rows.add(row);//добавляем ряд

        return new InlineKeyboardMarkup().setKeyboard(rows);
    }

    public InlineKeyboardMarkup keyboardZeroize() {

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();  //ряды
        List<InlineKeyboardButton> row = new ArrayList<>(); //ряд

        InlineKeyboardButton keyboardButton = new InlineKeyboardButton("Обнулить"); //имя кнопки
        keyboardButton.setCallbackData("quiz##:ze!ro&iz|e");  //начать
        row.add(keyboardButton); //добавляем кнопку

        rows.add(row);//добавляем ряд

        return new InlineKeyboardMarkup().setKeyboard(rows);
    }
}
