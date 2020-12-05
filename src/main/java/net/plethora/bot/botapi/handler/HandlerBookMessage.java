package net.plethora.bot.botapi.handler;

import net.plethora.bot.botapi.system.ShiftView;
import net.plethora.bot.dao.DataAccessMaterialBook;
import net.plethora.bot.dao.DataAccessSaveCell;
import net.plethora.bot.botapi.system.systemMessage.AgeOptionBookMessage;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

@Component
public class HandlerBookMessage<T> {

    private DataAccessMaterialBook dataAccessBook;
    private DataAccessSaveCell dataAccessSaveCell;
    private AgeOptionBookMessage ageOptionBookMessage;
    private ShiftView shiftView;

    public HandlerBookMessage(DataAccessMaterialBook dataAccessBook, AgeOptionBookMessage ageOptionBookMessage,
                              ShiftView shiftView, DataAccessSaveCell dataAccessSaveCell) {
        this.dataAccessBook = dataAccessBook;
        this.ageOptionBookMessage = ageOptionBookMessage;
        this.shiftView = shiftView;
        this.dataAccessSaveCell = dataAccessSaveCell;
    }

    public List<T> handler(long chatId, String msgUser, int messageId, String callBackId) {
//TODO сделать по аналогии с задачами, что бы прилетела тема книги
        List<T> msg = new ArrayList<>();
        String subject = "older";    //TODO изменится когда появиться чек как в Task

        if (msgUser.equals("older")) {   //поменять на тему из сообщения (сделать чек как в Task)
            msg = shiftView.view(chatId, subject, messageId, dataAccessBook, false, false);

        } else if (msgUser.equals("children")) {

        } else if (msgUser.length() >= 8 && msgUser.substring(0, 8).equals("%n->ex!t")) {
            msg = shiftView.view(chatId, subject, messageId, dataAccessBook, true, false);
        } else if (msgUser.length() >= 8 && msgUser.substring(0, 8).equals("%b->ac!k")) {
            msg = shiftView.view(chatId, subject, messageId, dataAccessBook, false, true);
        } else if (msgUser.equals(":previ!ew&")) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(callBackId);
            answerCallbackQuery.setText("Превью\n\n" + dataAccessBook.findById(dataAccessSaveCell
                    .findByChatIdAndSubject(chatId, subject)
                    .getSaveId()).getDescription());
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