package net.plethora.bot.botapi.system.systemMessage;

import com.google.inject.internal.cglib.proxy.$UndeclaredThrowableException;
import net.plethora.bot.botapi.keyboards.kbjob.KeyboardOptionsSearch;
import net.plethora.bot.model.systemmodel.InfoForSearch;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Component
public class SearchDataJobMessage {

    private KeyboardOptionsSearch keyboardOptionsSearch;

    public SearchDataJobMessage(KeyboardOptionsSearch keyboardOptionsSearch) {
        this.keyboardOptionsSearch = keyboardOptionsSearch;
    }

    public EditMessageText editMessage(long chatId, int messageId, InfoForSearch infoForSearch) {
        String area = infoForSearch.getArea();
        String period = infoForSearch.getPeriod();
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText("Запрос: Город [" + area + "], Период [" + period + "]");
        editMessageText.setReplyMarkup(keyboardOptionsSearch.keyboard(infoForSearch));
        return editMessageText;
    }

    public SendMessage message(long chatId, InfoForSearch infoForSearch) {
        String area = infoForSearch.getArea();
        String period = infoForSearch.getPeriod();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (area == null)
            area = "Не определен";
        if (period == null)
            period = "Не определен";

        sendMessage.setText("Запрос: Город [ " + area + " ], Период [ " + period + " ]");
        sendMessage.setReplyMarkup(keyboardOptionsSearch.keyboard(infoForSearch));
        return sendMessage;
    }
}