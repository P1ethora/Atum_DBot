package net.plethora.bot.dao;

import net.plethora.bot.model.Answer;
import net.plethora.bot.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;


public class Dialog {

    private Answer answer;
    private final String NEGATIVE_ANSWER = "хм... Про это ничего не знаю";
    @Autowired                               //Вынести в отдельный класс
    private PostRepository postRepository;

    public String getAnswer(String ask) {
        assignAnswer(ask);
        return answer != null ? answer.getAnswer() : NEGATIVE_ANSWER;
    }

    private void assignAnswer(String ask) {
        answer = postRepository.findAnswerByAsk(ask);
    }
}