package net.plethora.bot.dao.repo;

import net.plethora.bot.model.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositoryQuiz extends MongoRepository<Quiz,String> {

   public Quiz findByQuestion(String questions);

   public void deleteById(String id);

}
