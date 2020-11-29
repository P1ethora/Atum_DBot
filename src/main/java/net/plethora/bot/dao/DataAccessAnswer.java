package net.plethora.bot.dao;

import net.plethora.bot.model.Answer;
import net.plethora.bot.dao.repo.PostRepositoryAnswer;
import org.springframework.stereotype.Component;

/**
 * Соединяется с базой данных
 */

@Component
public class DataAccessAnswer {

    private final PostRepositoryAnswer postRepositoryAnswer;

    public DataAccessAnswer(PostRepositoryAnswer postRepositoryAnswer) {
        this.postRepositoryAnswer = postRepositoryAnswer;
    }

    public Answer handleRequest(String ask) {
        return postRepositoryAnswer.findAnswerByAsk(ask);
    }
}