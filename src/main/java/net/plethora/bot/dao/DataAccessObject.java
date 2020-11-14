package net.plethora.bot.dao;

import net.plethora.bot.model.Answer;
import net.plethora.bot.repo.PostRepositoryAnswer;
import org.springframework.stereotype.Component;

/**
 * Соединяется с базой данных
 */

@Component
public class DataAccessObject {

    private final PostRepositoryAnswer postRepositoryAnswer;

    public DataAccessObject(PostRepositoryAnswer postRepositoryAnswer) {
        this.postRepositoryAnswer = postRepositoryAnswer;
    }

    public Answer handleRequest(String ask) {
        return postRepositoryAnswer.findAnswerByAsk(ask);
    }
}