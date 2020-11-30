package net.plethora.bot.botapi.keyboards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Добавляет кнопки под условие задачи
 */
public class KeyboardTaskSelect {
    private String idTask;
    private String subject;
    private int limit;
    private int numberTask;

    public KeyboardTaskSelect(String idTask, String subject, int limit, int numberTask) {
        this.idTask=idTask;
        this.subject=subject;
        this.limit=limit;
        this.numberTask=numberTask;
    }

    /**
     *
     * @return готовую инлайн клавиатуру
     */
    public InlineKeyboardMarkup keyboard() {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(); //клава
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();  //ряды
        List<InlineKeyboardButton> rowTop = new ArrayList<>(); //ряд
        List<InlineKeyboardButton> rowDown = new ArrayList<>(); //ряд

        InlineKeyboardButton keyboardButtonBackToSubject = new InlineKeyboardButton("К разделам");
        keyboardButtonBackToSubject.setCallbackData(":BackToSubject");
        rowDown.add(keyboardButtonBackToSubject);

        InlineKeyboardButton keyboardButton = new InlineKeyboardButton("Решение"); //кнопка
        keyboardButton.setCallbackData(":!awr{" + idTask + "}");  //ответка в виде id задачи и небольшога шифра
        rowDown.add(keyboardButton);

        if (numberTask != 1) {
            InlineKeyboardButton keyboardButtonBack = new InlineKeyboardButton("Назад"); // кнопка
            keyboardButtonBack.setCallbackData("%b->ac!k{" + subject + "}");
            rowTop.add(keyboardButtonBack);
        }

        if (numberTask != limit) {
            InlineKeyboardButton keyboardButtonNext = new InlineKeyboardButton("Далее"); //кнопка
            keyboardButtonNext.setCallbackData("%n->ex!t{" + subject + "}");
            rowTop.add(keyboardButtonNext);
        }

        rows.add(rowTop);
        rows.add(rowDown);

        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }
}