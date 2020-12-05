package net.plethora.bot.dao.repo;

import net.plethora.bot.model.systemmodel.SaveCell;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositorySaveCell extends MongoRepository<SaveCell, String> {

    public SaveCell findByChatIdAndSaveSubject(long chatId, String subject);
}