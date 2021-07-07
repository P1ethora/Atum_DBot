package net.plethora.bot.botapi.keyboards.kbtask;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardSubjectTask {

    public InlineKeyboardMarkup inlineKeyboardSubjectTask() {

        List<List<InlineKeyboardButton>> rows = new ArrayList<>(); //список рядов
        List<InlineKeyboardButton> row = new ArrayList<>(); //
        List<InlineKeyboardButton> row1 = new ArrayList<>();// ряды
        List<InlineKeyboardButton> row2 = new ArrayList<>();//

        InlineKeyboardButton buttonV = new InlineKeyboardButton("Ветвления");       //
        InlineKeyboardButton buttonL = new InlineKeyboardButton("Линейные");        //
        InlineKeyboardButton buttonC = new InlineKeyboardButton("Циклы");           // название
        InlineKeyboardButton buttonM = new InlineKeyboardButton("Массивы");         //  кнопок
        InlineKeyboardButton buttonMM = new InlineKeyboardButton("Мн-мр-е массивы");//
        InlineKeyboardButton buttonS = new InlineKeyboardButton("Срт-ка массива");  //
        InlineKeyboardButton buttonD = new InlineKeyboardButton("Декомпозиция");    //

        buttonV.setCallbackData("task##ветвления");           //
        buttonL.setCallbackData("task##линейные");            //
        buttonC.setCallbackData("task##циклы");               // сообщения
        buttonM.setCallbackData("task##массивы");             //  запроса
        buttonMM.setCallbackData("task##многомерные массивы");//
        buttonS.setCallbackData("task##сортировка массива");  //
        buttonD.setCallbackData("task##декомпозиция");        //

        row.add(buttonV);  //
        row.add(buttonL);  //
        row.add(buttonC);  // распределяем
        row1.add(buttonM); //    кнопки
        row1.add(buttonMM);//   по рядам
        row1.add(buttonS); //
        row2.add(buttonD); //

        rows.add(row);
        rows.add(row1);
        rows.add(row2);

        return new InlineKeyboardMarkup(rows);
    }
}