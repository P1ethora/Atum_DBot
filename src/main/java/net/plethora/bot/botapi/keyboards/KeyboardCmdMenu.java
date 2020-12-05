package net.plethora.bot.botapi.keyboards;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardCmdMenu {

    public SendMessage process(long chatId) {
        SendMessage sendMessage = new SendMessage(chatId, "keyboard on");

        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow first = new KeyboardRow();
        first.add("ASK");
        first.add("TASK");
        first.add("JOB");
        first.add("HELP");

        rows.add(first);

        return sendMessage.setReplyMarkup(new ReplyKeyboardMarkup(rows).setSelective(true).setResizeKeyboard(true));
    }
}