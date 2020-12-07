package net.plethora.bot.botapi.keyboards.kbquiz;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardQuizSelect {
    public InlineKeyboardMarkup keyboard() {

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();  //ряды
        List<InlineKeyboardButton> row = new ArrayList<>(); //ряд

        InlineKeyboardButton keyboardButtonBackToSubject = new InlineKeyboardButton("Обнулить");
        keyboardButtonBackToSubject.setCallbackData(":ze!ro&iz|e");
        row.add(keyboardButtonBackToSubject);

        InlineKeyboardButton keyboardButton = new InlineKeyboardButton("Далее"); //кнопка
        keyboardButton.setCallbackData(":next!random");  //ответка в виде id задачи и небольшога шифра
        row.add(keyboardButton);

        rows.add(row);

        return new InlineKeyboardMarkup(rows);
    }
}