package net.plethora.bot.botapi.system.systemMessage;

import net.plethora.bot.botapi.keyboards.KeyboardAgeOptionBook;
import net.plethora.bot.dao.DataAccessDesingFile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

@Component
public class AgeOptionBookMessage {

    private DataAccessDesingFile dataAccessDesingFile;

    public AgeOptionBookMessage(DataAccessDesingFile dataAccessDesingFile) {
        this.dataAccessDesingFile = dataAccessDesingFile;
    }

    public EditMessageMedia editMessage(long idChat, int idMessage){
        EditMessageMedia editMessageMedia = new EditMessageMedia();
        editMessageMedia.setChatId(idChat); //id чата
        editMessageMedia.setMessageId(idMessage);  //id сообщения
        editMessageMedia.setMedia(new InputMediaPhoto().setMedia((dataAccessDesingFile.findByName("pictureOptions").getUrl())).setCaption("Выберите раздел:"));
        editMessageMedia.setReplyMarkup(new KeyboardAgeOptionBook().inlineKeyboardSubjectTask());
        return editMessageMedia;
    }

    public SendPhoto message(long chatId){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(dataAccessDesingFile.findByName("pictureOptions").getUrl());
        sendPhoto.setReplyMarkup(new KeyboardAgeOptionBook().inlineKeyboardSubjectTask());
        sendPhoto.setCaption("Выберите раздел:");

        return sendPhoto;
    }
}