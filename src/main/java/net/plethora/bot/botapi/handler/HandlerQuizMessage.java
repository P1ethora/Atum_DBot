package net.plethora.bot.botapi.handler;

import net.plethora.bot.dao.DataAccessQuiz;
import net.plethora.bot.model.Quiz;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class HandlerQuizMessage<T> {

    private DataAccessQuiz dataAccessQuiz;

    public HandlerQuizMessage(DataAccessQuiz dataAccessQuiz) {
        this.dataAccessQuiz = dataAccessQuiz;
    }

    public List<T> process(long chatId) throws TelegramApiRequestException {
        //TODO есть некая ответка на выбор варианта

        Quiz quiz = dataAccessQuiz.findRandom();

        List<T> messages = new ArrayList<>();
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

        messages.add((T) sendPoll);

        return messages;
    }
}