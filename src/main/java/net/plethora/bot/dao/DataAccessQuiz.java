package net.plethora.bot.dao;

import net.plethora.bot.dao.repo.PostRepositoryQuiz;
import net.plethora.bot.model.Quiz;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.Random;

@Component
public class DataAccessQuiz {

    private PostRepositoryQuiz postRepositoryQuiz;

    public DataAccessQuiz(PostRepositoryQuiz postRepositoryQuiz) {
        this.postRepositoryQuiz = postRepositoryQuiz;
    }

    public Quiz findRandom(){
        return postRepositoryQuiz.findAll().get(new Random().nextInt(postRepositoryQuiz.findAll().size()));
    }

    public Quiz findByQuestions(String questions) {
        return postRepositoryQuiz.findByQuestion(questions);
    }

}
