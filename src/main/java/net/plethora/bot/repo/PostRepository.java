package net.plethora.bot.repo;


import net.plethora.bot.model.Answer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends MongoRepository<Answer, String> {

    @Query("{ 'ask': ?0}")
    public Answer findAnswerByAsk(String ask);
}
