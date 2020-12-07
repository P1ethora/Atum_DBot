package net.plethora.bot.botapi.handler;

import net.plethora.bot.botapi.keyboards.kbquiz.KeyboardQuizSelect;
import net.plethora.bot.botapi.keyboards.kbquiz.KeyboardSoloButtonQuiz;
import net.plethora.bot.dao.DataAccessQuiz;
import net.plethora.bot.dao.DataAccessSaveCellQuiz;
import net.plethora.bot.model.Quiz;
import net.plethora.bot.model.systemmodel.SaveCellQuiz;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.*;

@Component
public class HandlerQuizMessage<T> {

    private DataAccessQuiz dataAccessQuiz;
    private DataAccessSaveCellQuiz dataAccessSaveCellQuiz;
    private KeyboardQuizSelect keyboardQuizSelect;
    private KeyboardSoloButtonQuiz keyboardSoloButtonQuiz;

    public HandlerQuizMessage(DataAccessQuiz dataAccessQuiz, DataAccessSaveCellQuiz dataAccessSaveCellQuiz,
                              KeyboardQuizSelect keyboardQuizSelect, KeyboardSoloButtonQuiz keyboardSoloButtonQuiz) {
        this.dataAccessQuiz = dataAccessQuiz;
        this.dataAccessSaveCellQuiz = dataAccessSaveCellQuiz;
        this.keyboardQuizSelect = keyboardQuizSelect;
        this.keyboardSoloButtonQuiz = keyboardSoloButtonQuiz;
    }

    public List<T> process(long chatId, String msgUser) {
        //TODO есть некая ответка на выбор варианта
        List<T> messages = new ArrayList<>();
        if (msgUser.equals(":next!random")) {//кнопка далее
            List<String> cellSaveQuiz = Arrays.asList(dataAccessSaveCellQuiz.findByChatId(chatId).getSaveIdQuiz());
            if (cellSaveQuiz.size() == 0) {
                messages.add((T) new SendMessage(chatId, "Поздравляю! Вы прошли всю викторину, при желании можно обнулить результат и пройти заново")
                        .setReplyMarkup(keyboardSoloButtonQuiz.keyboardZeroize()));
            } else {
                messages.add((T) getRandomPoll(chatId, cellSaveQuiz));
            }
        } else if (msgUser.equals(":ze!ro&iz|e")) { //кнопка обнулить
            List<String> cellSaveQuiz = dataAccessQuiz.findAllId();//все id викторины
            dataAccessSaveCellQuiz.editSaveCellQuiz(chatId, cellSaveQuiz.toArray(new String[0]));
            messages.add((T) new SendMessage(chatId, "Обнулено!"));
            messages.add((T) new SendMessage(chatId, "Что бы начать викторину нажмите старт")
                    .setReplyMarkup(keyboardSoloButtonQuiz.keyboardStart()));
        } else if (msgUser.equals(":sta!r>t")) { // кнопка старт
            SaveCellQuiz saveCellQuiz = dataAccessSaveCellQuiz.findByChatId(chatId); //ищем сохранения для пользователя
            if (saveCellQuiz == null) {
                List<String> allQuizId = dataAccessQuiz.findAllId(); //все id викторин
                saveCellQuiz = new SaveCellQuiz(chatId, allQuizId.toArray(new String[0])); //наполняем объект данными
                dataAccessSaveCellQuiz.newSaveCellQuiz(saveCellQuiz);  //отправлям в базу
                messages = (List<T>) getRandomPoll(chatId, Arrays.asList(saveCellQuiz.getSaveIdQuiz()));
            } else {

                List<String> cellSaveQuiz = Arrays.asList(saveCellQuiz.getSaveIdQuiz()); //список неиспользованных викторин

                if (cellSaveQuiz.size() == 0) {
                    messages.add((T) new SendMessage(chatId, "Поздравляю! Вы прошли всю викторину, при желании можно обнулить результат и пройти заново")
                            .setReplyMarkup(keyboardSoloButtonQuiz.keyboardZeroize()));

                } else {
                    messages.add((T) getRandomPoll(chatId, cellSaveQuiz));
                }
            }
        } else {
            messages.add((T) new SendMessage(chatId, "неверный формат ввода"));
        }
        return messages;
    }

    private SendPoll getRandomPoll(long chatId, List<String> cellSaveQuiz) {
        int numberRandom = new Random().nextInt(cellSaveQuiz.size());
        String quizSendId = cellSaveQuiz.get(numberRandom); //случайная викторина id

        Quiz quiz = dataAccessQuiz.findById(quizSendId);  //определяю викторину
        List<String> newSaveQuiz = new ArrayList<>();
        newSaveQuiz.addAll(cellSaveQuiz);
        newSaveQuiz.remove(quizSendId);//удаляю из листа

        dataAccessSaveCellQuiz.editSaveCellQuiz(chatId, newSaveQuiz.toArray(new String[0]));// редактируем сохранение

        SendPoll sendPoll = new SendPoll();

        sendPoll.setQuestion(quiz.getQuestion());
        sendPoll.setChatId(chatId);

        sendPoll.setAnonymous(true);
        sendPoll.setExplanation("О нет!");
        sendPoll.setType("quiz");
        sendPoll.setCorrectOptionId(quiz.getAnswer());

        List<String> options = Arrays.asList(quiz.getOptions());
        sendPoll.setOptions(options);
        sendPoll.setCorrectOptionId(quiz.getAnswer());
        sendPoll.setReplyMarkup(keyboardQuizSelect.keyboard());

        return sendPoll;
    }
}