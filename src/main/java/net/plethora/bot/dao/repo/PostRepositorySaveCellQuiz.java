package net.plethora.bot.dao.repo;

import net.plethora.bot.model.systemmodel.SaveCellQuiz;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositorySaveCellQuiz extends MongoRepository<SaveCellQuiz,String> {

    SaveCellQuiz findByChatId(long chatId);

}