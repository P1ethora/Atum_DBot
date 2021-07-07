package net.plethora.bot.dao.repo;

import net.plethora.bot.model.Answer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Метод поиска по базе данных
 */

@Repository
public interface PostRepositoryAnswer extends MongoRepository<Answer, String> {

    @Query("{ 'ask': ?0}")
   public Answer findAnswerByAsk(String ask);

    public void deleteById(String id);
}