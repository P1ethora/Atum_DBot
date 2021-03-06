package net.plethora.bot.botapi.keyboards.kbtask;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Добавляет кнопки под условие задачи
 */
@Component
public class KeyboardTaskSelect {

    public InlineKeyboardMarkup keyboard(String idTask, String subject, int limit, int numberTask) {

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();  //ряды
        List<InlineKeyboardButton> rowTop = new ArrayList<>(); //ряд
        List<InlineKeyboardButton> rowDown = new ArrayList<>(); //ряд

        InlineKeyboardButton keyboardButtonBackToSubject = new InlineKeyboardButton("К разделам");
        keyboardButtonBackToSubject.setCallbackData("task##:BackToSubject");
        rowDown.add(keyboardButtonBackToSubject);

        InlineKeyboardButton keyboardButton = new InlineKeyboardButton("Решение"); //кнопка
        keyboardButton.setCallbackData("task##:!awr{" + idTask + "}");  //ответка в виде id задачи и небольшога шифра
        rowDown.add(keyboardButton);

        if (numberTask > 1) {
            InlineKeyboardButton keyboardButtonBack = new InlineKeyboardButton("Назад"); // кнопка
            keyboardButtonBack.setCallbackData("task##%b->ac!k{" + subject + "}");
            rowTop.add(keyboardButtonBack);
        }

        if (numberTask < limit) {
            InlineKeyboardButton keyboardButtonNext = new InlineKeyboardButton("Далее"); //кнопка
            keyboardButtonNext.setCallbackData("task##%n->ex!t{" + subject + "}");
            rowTop.add(keyboardButtonNext);
        }

        rows.add(rowTop);
        rows.add(rowDown);

        return new InlineKeyboardMarkup(rows);
    }
}