package net.plethora.bot.botapi;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
public class Menu {

    private ReplyKeyboardMarkup keyboard;
    private boolean toggleMenu;

    public SendMessage process(Update update) {
        long idChat = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage(idChat,"keyboard on");

        keyboard = new ReplyKeyboardMarkup();
        keyboard.setSelective(true);
        keyboard.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow first = new KeyboardRow();
        first.add("ASK");
        first.add("TASK");
        first.add("JOB");
        first.add("HELP");

        rows.add(first);

        keyboard.setKeyboard(rows);
        toggleMenu = true;
        return sendMessage.setReplyMarkup(keyboard);
    }
}