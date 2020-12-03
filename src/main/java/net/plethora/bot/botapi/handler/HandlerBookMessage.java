package net.plethora.bot.botapi.handler;

import net.plethora.bot.botapi.keyboards.KeyboardBookSelect;
import net.plethora.bot.dao.DataAccessBook;
import net.plethora.bot.dao.DataAccessUser;
import net.plethora.bot.model.Book;
import net.plethora.bot.model.User;
import net.plethora.bot.systemMessage.AgeOptionBookMessage;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import java.util.ArrayList;
import java.util.List;

@Component
public class HandlerBookMessage<T> {

    private DataAccessBook dataAccessBook;
    private DataAccessUser dataAccessUser;
    private AgeOptionBookMessage ageOptionBookMessage;

    public HandlerBookMessage(DataAccessBook dataAccessBook, DataAccessUser dataAccessUser, AgeOptionBookMessage ageOptionBookMessage) {
        this.dataAccessBook = dataAccessBook;
        this.dataAccessUser = dataAccessUser;
        this.ageOptionBookMessage = ageOptionBookMessage;
    }

    public List<T> handler(long chatId, String msgUser, User user, int messageId, String callBackId) {
//TODO сделать по аналогии с задачами, что бы прилетела тема книги
        List<T> msg = new ArrayList<>();
        List<Book> allBooks = dataAccessBook.findAll();

        if (user.getIdSaveBook() == null) {
            user.setIdSaveBook(allBooks.get(0).getId());
            dataAccessUser.editUser(user, allBooks.get(0).getId());
        }
//открываем книгу сохраненную у пользователя
        if (msgUser.equals("older")) {
            List<Book> allBooksOlder = dataAccessBook.findAll();

            if (user.getIdSaveBook() == null) {
                user.setIdSaveBook(allBooks.get(0).getId());
                dataAccessUser.editUser(user, allBooks.get(0).getId());
            }
            for (int i = 0; i < allBooks.size(); i++) {
                if (allBooks.get(i).getId().equals(user.getIdSaveBook())) {
                    EditMessageMedia editMessageMedia = new EditMessageMedia();
                    editMessageMedia.setChatId(chatId); //id чата
                    editMessageMedia.setMessageId(messageId);  //id сообщения
                    editMessageMedia.setMedia(new InputMediaPhoto()
                            .setMedia(allBooks.get(i).getUrlCoverBook()));
                    editMessageMedia.setReplyMarkup(new KeyboardBookSelect(i + 1, allBooks.size(), allBooks.get(i).getUrl()).keyboard());
                    msg.add((T) editMessageMedia);
                    break;
                }
            }
        } else if (msgUser.equals("children")) {
            for (int i = 0; i < allBooks.size(); i++) {
                if (allBooks.get(i).getId().equals(user.getIdSaveBook())) {
                    EditMessageMedia editMessageMedia = new EditMessageMedia();
                    editMessageMedia.setChatId(chatId); //id чата
                    editMessageMedia.setMessageId(messageId);  //id сообщения
                    editMessageMedia.setMedia(new InputMediaPhoto()
                            .setMedia(allBooks.get(i).getUrlCoverBook()));
                    editMessageMedia.setReplyMarkup(new KeyboardBookSelect(i + 1, allBooks.size(), allBooks.get(i).getUrl()).keyboard());
                    msg.add((T) editMessageMedia);
                    break;
                }
            }
        } else if (msgUser.length() >= 8 && msgUser.substring(0, 8).equals("%n->ex!t")) {
            for (int i = 0; i < allBooks.size(); i++) {
                if (allBooks.get(i).getId().equals(user.getIdSaveBook())) {
                    EditMessageMedia editMessageMedia = new EditMessageMedia();
                    editMessageMedia.setChatId(chatId); //id чата
                    editMessageMedia.setMessageId(messageId);  //id сообщения
                    editMessageMedia.setMedia(new InputMediaPhoto()
                            .setMedia(allBooks.get(i + 1).getUrlCoverBook()));
                    dataAccessUser.editUser(user, allBooks.get(i + 1).getId());
                    editMessageMedia.setReplyMarkup(new KeyboardBookSelect(i + 2, allBooks.size(), allBooks.get(i + 1).getUrl()).keyboard());
                    msg.add((T) editMessageMedia);
                }
            }

        } else if (msgUser.length() >= 8 && msgUser.substring(0, 8).equals("%b->ac!k")) {
            for (int i = 0; i < allBooks.size(); i++) {
                if (allBooks.get(i).getId().equals(user.getIdSaveBook())) {
                    EditMessageMedia editMessageMedia = new EditMessageMedia();
                    editMessageMedia.setChatId(chatId); //id чата
                    editMessageMedia.setMessageId(messageId);  //id сообщения
                    editMessageMedia.setMedia(new InputMediaPhoto()
                            .setMedia(allBooks.get(i - 1).getUrlCoverBook()));
                    dataAccessUser.editUser(user, allBooks.get(i - 1).getId());
                    editMessageMedia.setReplyMarkup(new KeyboardBookSelect(i, allBooks.size(), allBooks.get(i - 1).getUrl()).keyboard());
                    msg.add((T) editMessageMedia);
                }
            }
        } else if (msgUser.equals(":previ!ew&")) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(callBackId);
            answerCallbackQuery.setText("Превью\n\n" + dataAccessBook.findById(user.getIdSaveBook()).getDescription());
            answerCallbackQuery.setShowAlert(true);
            msg.add((T) answerCallbackQuery);
        } else if (msgUser.equals(":to<opt!io>ns")) {
            msg.add((T) ageOptionBookMessage.editMessage(chatId, messageId)); //Возвращаем выбор книг по возрасту
        } else {
            msg.add((T) new SendMessage(chatId, "Неверная форма ввода"));
        }
        return msg;

    }
}