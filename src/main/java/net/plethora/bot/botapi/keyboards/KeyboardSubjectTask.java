package net.plethora.bot.botapi.keyboards;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardSubjectTask {
//TODO добавить все клавы сюда
    public InlineKeyboardMarkup inlineKeyboardSubjectTask() {

        InlineKeyboardMarkup inlineKeyBoard = new InlineKeyboardMarkup();   //наша клава под task
        List<List<InlineKeyboardButton>> rows = new ArrayList<>(); //список рядов
        List<InlineKeyboardButton> row = new ArrayList<>();//ряд
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();

        InlineKeyboardButton buttonV = new InlineKeyboardButton("Ветвления");
        InlineKeyboardButton buttonL = new InlineKeyboardButton("Линейные");
        InlineKeyboardButton buttonC = new InlineKeyboardButton("Циклы");
        InlineKeyboardButton buttonM = new InlineKeyboardButton("Массивы");
        InlineKeyboardButton buttonMM = new InlineKeyboardButton("Мн-мр-е массивы");
        InlineKeyboardButton buttonS = new InlineKeyboardButton("Срт-ка массива");
        InlineKeyboardButton buttonD = new InlineKeyboardButton("Декомпозиция");

        buttonV.setCallbackData("ветвления");
        buttonL.setCallbackData("линейные");
        buttonC.setCallbackData("циклы");
        buttonM.setCallbackData("массивы");
        buttonMM.setCallbackData("многомерные массивы");
        buttonS.setCallbackData("сортировка массива");
        buttonD.setCallbackData("декомпозиция");

        row.add(buttonV);
        row.add(buttonL);
        row.add(buttonC);
        row1.add(buttonM);
        row1.add(buttonMM);
        row1.add(buttonS);
        row2.add(buttonD);

        rows.add(row);
        rows.add(row1);
        rows.add(row2);
        inlineKeyBoard.setKeyboard(rows);

        return inlineKeyBoard;
    }
}
