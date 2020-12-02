package net.plethora.bot.botapi.handler.handbook;

import net.plethora.bot.botapi.handler.handtask.SubjectTaskUser;
import net.plethora.bot.botapi.keyboards.KeyboardBookSelect;
import net.plethora.bot.botapi.keyboards.KeyboardSubjectTask;
import net.plethora.bot.dao.DataAccessBook;
import net.plethora.bot.dao.DataAccessUser;
import net.plethora.bot.model.Book;
import net.plethora.bot.model.User;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import java.util.ArrayList;
import java.util.List;

@Component
public class HandlerBookMessage<T> {

    private DataAccessBook dataAccessBook;
    private DataAccessUser dataAccessUser;

    public HandlerBookMessage(DataAccessBook dataAccessBook,DataAccessUser dataAccessUser) {
        this.dataAccessBook = dataAccessBook;
        this.dataAccessUser = dataAccessUser;
    }

    public List<T> handler(long chatId, String msgUser, User user, int messageId){

        List<T> msg = new ArrayList<>();
        List<Book> allBooks = dataAccessBook.findAll();

        if(user.getIdSaveBook()==null){
            user.setIdSaveBook(allBooks.get(0).getId());
            dataAccessUser.editUser(user,allBooks.get(0).getId());
        }
//открываем книгу сохраненную у пользователя
        int numberBook = 0;
        for (Book book: allBooks) {
            numberBook++;
            if (book.getId().equals(user.getIdSaveBook())) {
//                SendPhoto sendPhoto = new SendPhoto();
//                //Есть вариант миновать сервер, телега сама может скачать файл и передать
//                sendPhoto.setChatId(chatId);    //id чата
//                sendPhoto.setPhoto(new InputFile().setMedia(book.getUrl()));  //путь
//                sendPhoto.setCaption(book.getDescription());  //описание
//                SendMessage sendMessage = new SendMessage();
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(book.getUrlCoverBook() +" "+ book.getDescription());
                msg.add((T) sendMessage.setReplyMarkup(new KeyboardBookSelect(numberBook,allBooks.size(),book.getUrl()).keyboard()));
            }
        }


        if (msgUser.length() >= 8 && msgUser.substring(0, 8).equals("%n->ex!t")) {

            //String subject = getValueMsg(msgUser);

            for (int i = 0; i<allBooks.size();i++) {
                if (allBooks.get(i).getId().equals(user.getIdSaveBook())) {
                    SendPhoto sendPhoto = new SendPhoto();
                    //Есть вариант миновать сервер, телега сама может скачать файл и передать
                    sendPhoto.setChatId(chatId);
                    sendPhoto.setPhoto(new InputFile().setMedia(allBooks.get(i+1).getUrl()));
                    sendPhoto.setCaption(allBooks.get(i).getDescription());
                    msg.add((T) sendPhoto.setReplyMarkup(new KeyboardBookSelect(i,allBooks.size(),allBooks.get(i).getUrl()).keyboard()));
                }
            }

        } else if (msgUser.length() >= 8 && msgUser.substring(0, 8).equals("%b->ac!k")) {
            for (int i = 0; i<allBooks.size();i++) {
                if (allBooks.get(i).getId().equals(user.getIdSaveBook())) {
                    //SendPhoto sendPhoto = new SendPhoto();
                    EditMessageMedia editMessageMedia = new EditMessageMedia();
                    EditMessageCaption messageCaption = new EditMessageCaption();
                    //messageCaption.;
                    editMessageMedia.setChatId(chatId);
                    editMessageMedia.setMessageId(messageId);
                    editMessageMedia.setReplyMarkup(new KeyboardBookSelect(i,allBooks.size(),allBooks.get(i).getUrl()).keyboard());
                    editMessageMedia.setMedia(new InputMediaPhoto().setMedia(allBooks.get(i).getUrl()));
                    //editMessageMedia.
                    //Есть вариант миновать сервер, телега сама может скачать файл и передать
                    //sendPhoto.setChatId(chatId);
                    //sendPhoto.setPhoto(new InputFile().setMedia(allBooks.get(i-1).getUrl()));
                    //sendPhoto.setCaption(allBooks.get(i).getDescription());
                    msg.add((T) editMessageMedia);
                }
            }
        } else {
            msg.add((T) new SendMessage(chatId, "Неверная форма ввода, книги предоставляются посредством выбора языка"));
        }
        return msg;

    }

}
