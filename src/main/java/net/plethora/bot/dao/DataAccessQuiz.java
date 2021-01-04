package net.plethora.bot.dao;

import net.plethora.bot.dao.repo.PostRepositoryQuiz;
import net.plethora.bot.model.Quiz;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataAccessQuiz {

    private PostRepositoryQuiz postRepositoryQuiz;

    public DataAccessQuiz(PostRepositoryQuiz postRepositoryQuiz) {
        this.postRepositoryQuiz = postRepositoryQuiz;
    }

    public Quiz findRandom() {
        return postRepositoryQuiz.findAll().get(new Random().nextInt(postRepositoryQuiz.findAll().size()));
    }

    public Quiz findById(String id){
        return postRepositoryQuiz.findById(id).orElseThrow(() -> new IllegalStateException("Quiz with id " + id + " not found"));
    }

    public List<String> findAllId(){
        List<String> idList = new ArrayList<>();
        for(Quiz quiz : postRepositoryQuiz.findAll()) {
           idList.add(quiz.getId());
        }
        return idList;
    }

    public Quiz findByQuestions(String questions) {
        return postRepositoryQuiz.findByQuestion(questions);
    }

    public List<Quiz> findAll() {
        return postRepositoryQuiz.findAll();
    }

    public void delete(String id) {
        postRepositoryQuiz.deleteById(id);
    }

    public void save(Quiz quiz) {
        postRepositoryQuiz.save(quiz);
    }

}