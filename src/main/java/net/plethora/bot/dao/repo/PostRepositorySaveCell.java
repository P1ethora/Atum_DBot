package net.plethora.bot.dao.repo;

import net.plethora.bot.model.systemmodel.SaveCellMaterial;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositorySaveCell extends MongoRepository<SaveCellMaterial, String> {

  public SaveCellMaterial findByChatIdAndSaveSubject(long chatId, String subject);
}