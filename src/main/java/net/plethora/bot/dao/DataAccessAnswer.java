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

    public Answer findById(String id) {
        return postRepositoryAnswer.findById(id).orElseThrow(() -> new IllegalStateException("Answer with id " + id + " not found"));
    }

    public void save(Answer answer) {
        postRepositoryAnswer.save(answer);
    }

    public void delete(String id) {
        postRepositoryAnswer.deleteById(id);
    }
}