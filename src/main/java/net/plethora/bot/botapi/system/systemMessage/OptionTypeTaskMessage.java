package net.plethora.bot.botapi.system.systemMessage;

import net.plethora.bot.botapi.keyboards.kbtask.KeyboardSubjectTask;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Component
public class OptionTypeTaskMessage {

    private KeyboardSubjectTask keyboardSubjectTask;

    public OptionTypeTaskMessage(KeyboardSubjectTask keyboardSubjectTask) {
        this.keyboardSubjectTask = keyboardSubjectTask;
    }

    public EditMessageText editMessage(long chatId, int messageId) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText("Разделы с задачами:");
        editMessageText.setReplyMarkup(keyboardSubjectTask.inlineKeyboardSubjectTask());
        return editMessageText;
    }

    public SendMessage message(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Разделы с задачами:");
        sendMessage.setReplyMarkup(keyboardSubjectTask.inlineKeyboardSubjectTask());
        return sendMessage;
    }

}
