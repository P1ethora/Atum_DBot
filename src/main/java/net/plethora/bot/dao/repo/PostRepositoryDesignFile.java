package net.plethora.bot.dao.repo;

import net.plethora.bot.model.systemmodel.DesignFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositoryDesignFile extends MongoRepository<DesignFile,String> {

    public DesignFile findByFileName(String fileName);
}