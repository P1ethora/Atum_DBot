package net.plethora.bot.dao;

import net.plethora.bot.model.Answer;
import net.plethora.bot.repo.PostRepository;
import org.springframework.stereotype.Component;

/**
 * Соединяется с базой данных
 */

@Component
public class DataAccessObject {

    private final PostRepository postRepository;

    public DataAccessObject(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Answer handleRequest(String ask) {
        return postRepository.findAnswerByAsk(ask);
    }
}