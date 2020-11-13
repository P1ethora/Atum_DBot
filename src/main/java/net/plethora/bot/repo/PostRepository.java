package net.plethora.bot.repo;


import net.plethora.bot.model.Answer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * Метод поиска по базе данных
 */

@Repository
public interface PostRepository extends MongoRepository<Answer, String> {

    @Query("{ 'ask': ?0}")
    Answer findAnswerByAsk(String ask);
}
